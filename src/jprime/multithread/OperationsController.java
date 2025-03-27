package jprime.multithread;

import java.util.ArrayDeque;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class OperationsController {
    private static final int NB_OPERATIONS_WANTED_BY_BLOCK_BEFORE_AWAIT = 5;

    private final BlockOperationsModel blockOperationsModel = new BlockOperationsModel();
    private final ArrayBlockingQueue<MultOperationModel> queueTerminatedOperations = new ArrayBlockingQueue<>(90);
    private final ExecutorService threadsPerBlock = Executors.newFixedThreadPool(90);
    private final ReentrantLock lock = new ReentrantLock();
    private final ReentrantLock lockCompletion = new ReentrantLock();
    private final ReentrantLock lockCounterAdded = new ReentrantLock();
    private final ReentrantLock lockCounterTerminated = new ReentrantLock();
    private final Condition allQueuesCanHaveMoreElements = lock.newCondition();
    private final Condition allOperationsAreTreated = lockCompletion.newCondition();

    private int nbOperationsAdded = 0, nbOperationsTerminated = 0;
    private int probablyQueueWithMinElements = 0;
    private boolean isAwait;

    private final transient long[] tab;
    private final int size;

    public OperationsController(long[] tab) {
        this.tab = tab;
        this.size = tab.length;
    }

    //mono thread
    public void addNewOperation(int ib, int jb, int iBlock, int indexLong, long bitsMask, int iTabInit, int vMult, boolean ignoreFirstStep) {
        if (ignoreFirstStep && iTabInit + vMult >= size) return;
        if (!ignoreFirstStep && iTabInit >= size) return;

        MultOperationModel operation = new MultOperationModel((short) ib, (short) jb, iBlock, (byte) indexLong, bitsMask, iTabInit, vMult, ignoreFirstStep);

        BlockOperationsByIndex blockOperationForIndex = blockOperationsModel.getForIndex(indexLong);
        ArrayDeque<MultOperationModel> queueOperations = blockOperationForIndex.getQueueOperations();
        int nbOperations;
        synchronized (queueOperations) {
            queueOperations.addLast(operation);
            nbOperations = queueOperations.size();
            lockCounterAdded.lock();
            nbOperationsAdded++;
            lockCounterAdded.unlock();
        }

        if (nbOperations == 1) {
            //start thread;
            calculate(operation);
            probablyQueueWithMinElements = 1;   //it's probably false, but it's 0 or 1;
        } else if (nbOperations <= probablyQueueWithMinElements + 1) {
            probablyQueueWithMinElements = nbOperations;

            if (probablyQueueWithMinElements == NB_OPERATIONS_WANTED_BY_BLOCK_BEFORE_AWAIT) {
                for (int i = 89; i >= 0; i--) {
                    int nb = blockOperationsModel.getForIndex(i).getQueueOperations().size();
                    if (nb < probablyQueueWithMinElements) {
                        probablyQueueWithMinElements = nb;
                        break;
                    }
                }
                if (probablyQueueWithMinElements == NB_OPERATIONS_WANTED_BY_BLOCK_BEFORE_AWAIT) {
                    lock.lock();
                    try {
                        StringBuilder sb = new StringBuilder(180);
                        for (int i = 0; i < 90; i++) {
                            sb.append(blockOperationsModel.getForIndex(i).getQueueOperations().size()).append(' ');
                        }
                        System.out.println(sb.toString());
                        isAwait = true;
                        allQueuesCanHaveMoreElements.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    lock.unlock();
                }
            }
        }

    }

    //multi thread
    public void addTerminatedOperation(final MultOperationModel operationTerminated) {
        int indexLong = operationTerminated.indexLong();
        BlockOperationsByIndex blockOperationForIndex = blockOperationsModel.getForIndex(indexLong);
        ArrayDeque<MultOperationModel> queueOperations = blockOperationForIndex.getQueueOperations();
        int nbOperations;
        MultOperationModel newOperation;
        synchronized (queueOperations) {
            queueOperations.removeFirst();
            nbOperations = queueOperations.size();
            newOperation = queueOperations.peekFirst();
            lockCounterTerminated.lock();
            nbOperationsTerminated++;
            lockCounterTerminated.unlock();
        }

//        if (operationTerminated.ib() % 360 == 0 && operationTerminated.jb() % 360 == 0)
//            System.out.println("operation : " + operationTerminated.ib() + " " + operationTerminated.jb() + " "
//                    + operationTerminated.initTab() + " " + operationTerminated.vMult() + " terminated \t progression: " + nbOperationsTerminated + "/" + nbOperationsAdded);

        if (newOperation != null) {
            calculate(newOperation);
        } else if (nbOperationsAdded == nbOperationsTerminated) {
            lockCompletion.lock();
            allOperationsAreTreated.signal();
            lockCompletion.unlock();
        }


        if (nbOperations <= probablyQueueWithMinElements) {
            probablyQueueWithMinElements = nbOperations;
            if (isAwait && probablyQueueWithMinElements <= NB_OPERATIONS_WANTED_BY_BLOCK_BEFORE_AWAIT - 1) {
                lock.lock();
                isAwait = false;
                allQueuesCanHaveMoreElements.signal();  //can deblock main thread.
                lock.unlock();
            }
        }
    }

    //final step
    public void waitAllThreadAreTerminated() {
        lockCompletion.lock();
        while (nbOperationsAdded != nbOperationsTerminated) {
            try {
                allOperationsAreTreated.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        lockCompletion.unlock();

        threadsPerBlock.shutdown();

        while (true) {
            try {
                if (threadsPerBlock.awaitTermination(10, TimeUnit.SECONDS)) {
                    break;
                } else {
                    System.out.println("On est a : " + nbOperationsTerminated + " sur " + nbOperationsAdded);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void calculate(final MultOperationModel operation) {
        final long bitsMask = operation.bitsMask();
        final int vMult = operation.vMult();

        final boolean ignoreFirstStep = operation.ignoreFirstStep();
        final int iTabInitBase = operation.initTab();
        final int iTabInit = ignoreFirstStep ? iTabInitBase + vMult : iTabInitBase;

        threadsPerBlock.submit(() -> {
            for (int iTab = iTabInit; iTab < size; iTab += vMult)    //els[ib]*(30030k+1) with k>=1 --> 17*30031, 17*60061, 17*90091, 17*120121
            {
                tab[iTab] |= bitsMask;
                //(17*1     =      17 =>      17%30030=17    =>      17/30030= 0)
                // 17*30031 =  510527 =>  510527%30030=17    =>  510527/30030=17
                // 17*60061 = 1021037 => 1021037%30030=17    => 1021037/30030=34
                //=> 17*(30030k+1) %30030 don't change with +30030 in multiplier, and +17 in tab index for each +30030
            }
            addTerminatedOperation(operation);
        });
    }
}
