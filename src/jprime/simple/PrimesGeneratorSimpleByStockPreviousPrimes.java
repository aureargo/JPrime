package jprime.simple;

import java.util.ArrayList;

public class PrimesGeneratorSimpleByStockPreviousPrimes
{
    private static final int BLOCK = 6_000;

    public static void main(String[] args)
    {
        long start = System.currentTimeMillis();

        compute(100_000_000);
        //1_000_000     -> 109 ms           => 78498
        //10_000_000    -> 1339 ms          => 664579
        //100_000_000   -> 28628 ms         => 5761455
        //500_000_000   ->  ms              => 26355867
        //1_000_000_000 ->  ms              => 50847534
        //Integer.MAX_VALUE ->  ms          => 105097565
        //10_000_000_000l   ->  ms          => 455052511
        //32_000_000_000l   ->  ms          => 1382799415

        long end = System.currentTimeMillis();
        System.out.println("time of execution = " + (end - start) + " ms");
    }

    private static void compute(final int n)
    {
        ArrayList<Integer> tab = PrimesSimpleByAdd.allocListInFoncOfN(n);

        int lastX = 0;
//        double precision = 0;
//        int nbAnomalies = 0;

        if (n >= 2)
        {
            tab.add(2);
            if (n >= 3)
            {
                tab.add(3);

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

//                        int size = (int) Math.ceil((x / Math.log(x))*1.25);
//                        if (size < tab.size())
//                        {
//                            double prec = tab.size() / (double) size;
//                            nbAnomalies++;
//                            if (prec > precision)
//                            {
//                                precision = prec;
//                            }
//                        }
                    }

                    int size = tab.size();
                    if (size % 500_000 == 0 && !findDiv)
                    {
                        float percent = x * 100.0f / n;
                        System.out.println(percent + "%\t" + " => " + size + " elements \t" + x + " (+" + (x - lastX) + ")");
//                        System.out.println("precision = " + precision + " / nbAnomalies = " + nbAnomalies);
                        lastX = x;
                    }

                }
            }
        }

        int size = tab.size();
        System.out.println("nb of primary numbers = " + size);
//            System.out.println("precision = " + precision + " / nbAnomalies = " + nbAnomalies);

//            StringBuilder stringBuilder = new StringBuilder(BLOCK * 4);
//            int i = 0;
//            int max = BLOCK;
//            int lastI = 0;
//            while (i < size)
//            {
//                int val = tab.get(i);
//                stringBuilder.append(val);
//                i++;
//                while (i < size)
//                {
//                    val = tab.get(i);
//                    if (val < max)
//                    {
//                        stringBuilder.append(';').append(val);
//                        i++;
//                    }
//                    else
//                    {
//                        break;
//                    }
//                }
//                System.out.println((i - lastI) + " => [" + stringBuilder.toString() + ']');
//                stringBuilder.delete(0, Integer.MAX_VALUE); //clear() (if only it's implement in StringBuilder class)
//                max += BLOCK;
//                lastI = i;
//            }
    }

}
