package jprime.simple;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrimesSimpleByAddMultiThread
{
    private static final int BLOCK = 6_000;
    private static final int BLOCK_THREAD = 1_000_000;    //1_000_000 => 93 ms of execution

    public static void main(String[] args)
    {
        long startTime = System.currentTimeMillis();
        
        compute(2_147_000_000);
        //10_000_000    => 1 000 ms
        //100_000_000   => 6 000 ms
        //500_000_000   => 61 394 ms
        //2_000_000_000 => 360 694 ms
            
        long endTime = System.currentTimeMillis();
        System.out.println("time of execution = " + (endTime - startTime) + " ms");
    }

    private static void compute(final int n)
    {
        ArrayList<Integer> tab = PrimesSimpleByAdd.allocListInFoncOfN(n);

        int lastX = 0;
        float lastPercent = 1;

        if (n >= 2)
        {
            tab.add(2);
            if (n >= 3)
            {
                tab.add(3);

                int nbInFirstLoop;
                if (n < BLOCK_THREAD)
                {
                    nbInFirstLoop = n;
                }
                else if (n <= 3 * BLOCK_THREAD)
                {
                    nbInFirstLoop = n;
                }
                else
                {
                    nbInFirstLoop = BLOCK_THREAD;
                }
                firstLoop(nbInFirstLoop, tab);

                if (n > 3 * BLOCK_THREAD)
                {
                    int nbRemainingVal = n - BLOCK_THREAD;
                    int nbThreads = (int) Math.ceil(nbRemainingVal / BLOCK_THREAD);
                    int nbValByThread = (int) Math.floor(nbRemainingVal / nbThreads);

                    ExecutorService executorService = Executors.newFixedThreadPool(16);//newCachedThreadPool();//newFixedThreadPool(8);
                    Future<ArrayList<Integer>> futures[] = new Future[nbThreads];
                    int start = BLOCK_THREAD + 1;
                    for (int iThread = 0; iThread < nbThreads; iThread++)
                    {
                        int end = (iThread == nbThreads - 1) ? n : start + nbValByThread - 1;
                        PrimeNumberTask task = new PrimeNumberTask(tab, start, end);
                        futures[iThread] = executorService.submit(task);

                        start = end + 1;
                    }

                    for (int iThread = 0; iThread < nbThreads; iThread++)
                    {
                        Future<ArrayList<Integer>> future = futures[iThread];
                        try
                        {
                            ArrayList<Integer> res = future.get();
                            tab.addAll(res);
                        }
                        catch (InterruptedException ex)
                        {
                            Logger.getLogger(PrimesSimpleByAddMultiThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        catch (ExecutionException ex)
                        {
                            Logger.getLogger(PrimesSimpleByAddMultiThread.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        futures[iThread] = null;

                        int size = tab.size();
                        int x = tab.get(size - 1);
                        float percent = x * 100.0f / n;
                        if (percent >= lastPercent)
                        {
                            System.out.println(percent + "%\t" + " => " + size + " elements \t" + x + " (+" + (x - lastX) + ")");
                            //                        System.out.println("precision = " + precision + " / nbAnomalies = " + nbAnomalies);
                            lastPercent += 1;
                            lastX = x;
                        }

                    }
                    executorService.shutdown();
                }


            }

        }

        int size = tab.size();
        System.out.println("nb of primary numbers = " + size);
    }

    private static void firstLoop(int n, ArrayList<Integer> tab)
    {
        for (int x = 5; x <= n && x > 0; x += 2)    //x > 0 -> we don't go to more than Integer.MAX_VALUE
        {
            boolean findDiv = false;
            int i = 1;
            int sqrtX = (int) Math.sqrt(x);
            int div = 0;

            while (div <= sqrtX && i < tab.size() && !findDiv)
            {
                div = tab.get(i);
                int mod = x % div;
                if (mod == 0)
                {
                    findDiv = true;
                }
                else
                {
                    i++;
                }
            }
            if (!findDiv)
            {
                tab.add(x);
            }
        }
    }

    //theoreme de fonction de compte des nombres premiers de Legendre-Gauss
    private static int piPrime(int n)
    {
        int size = (int) Math.ceil(n / Math.log(n));
        return size;
    }

    private static class PrimeNumberTask implements Callable<ArrayList<Integer>>
    {
        private final ArrayList<Integer> tabInit;
        private final int start;
        private final int end;

        public PrimeNumberTask(final ArrayList<Integer> tabInit, int start, int end)
        {
            this.tabInit = tabInit;
            this.start = start % 2 == 0 ? start + 1 : start;    //commencer par un nombre impair.
            this.end = end;
        }

        @Override
        public ArrayList<Integer> call() throws Exception
        {
            int sizeBeforeStart = piPrime(start);
            int sizeBeforeEnd = piPrime(end);
            int size = (int) ((sizeBeforeEnd - sizeBeforeStart) * 1.1);

            ArrayList<Integer> tabResult = new ArrayList<>(size);

            for (int x = start; x <= end; x += 2)
            {
                boolean findDiv = false;
                int i = 1;
                int sqrtX = (int) Math.sqrt(x);
                int div = 0;

                while (div <= sqrtX && i < tabInit.size() && !findDiv)
                {
                    div = tabInit.get(i);
                    int mod = x % div;
                    if (mod == 0)
                    {
                        findDiv = true;
                    }
                    else
                    {
                        i++;
                    }
                }
                if (!findDiv)
                {
                    tabResult.add(x);
                }

                if (x == Integer.MAX_VALUE)
                {
                    break;
                }
            }

            return tabResult;
        }
    }

}
