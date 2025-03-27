package jprime.multithread;

import java.util.ArrayDeque;

public class BlockOperationsByIndex {
    private static final int WANTED_WAITING_OPERATION = 2;
    private final int indexLong;
    private final ArrayDeque<MultOperationModel> queueOperations = new ArrayDeque<>(15);

    public BlockOperationsByIndex(int indexLong) {
        this.indexLong = indexLong;
    }

    public ArrayDeque<MultOperationModel> getQueueOperations() {
        return queueOperations;
    }

    public void addOperation(MultOperationModel operation) {
        queueOperations.addLast(operation);
    }

//    public void notifyOperationTerminated() {
//        MultOperationModel operationToExecute = null;
//        synchronized (queueOperations) {
//            if(!queueOperations.isEmpty()) {
//                operationToExecute = queueOperations.pollFirst();
//            }
//        }
//
//        if(operationToExecute != null) {
//            executeOperation(operationToExecute)
////            executorService.submit()
//        }
//    }
//
//    private void executeOperation(final MultOperationModel operationToExecute) {
//    }
}
