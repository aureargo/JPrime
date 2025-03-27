/*
 * Copyright (C) août 2020
 * Thales LAS France SAS - all rights reserved
 */
package jprime.simple;

import java.util.ArrayList;

public class PrimesGeneratorShortSimpleByStockPreviousPrimes
{
    public static void main(String[] args)
    {
        long start = System.currentTimeMillis();

        compute(Short.MAX_VALUE);

        long end = System.currentTimeMillis();
        System.out.println("time of execution = " + (end - start) + " ms");
    }

    private static void compute(final short n)
    {
        ArrayList<Short> tab = allocListInFoncOfN(n);

        if (n >= 2)
        {
            tab.add((short) 2);
            if (n >= 3)
            {
                tab.add((short) 3);

                for (short x = 5; x <= n && x > 0; x += 2)  //x > 0 -> we don't go to more than Short.MAX_VALUE
                {
                    boolean findDiv = false;
                    int i = 1;
                    short sqrtX = (short) Math.sqrt(x);
                    short div = 0;

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

    private static ArrayList<Short> allocListInFoncOfN(short n)
    {
        //theoreme de fonction de compte des nombres premiers de Legendre-Gauss (nbPremier == (environ) n/ln(n) )
        int size = (int) Math.ceil((n / Math.log(n)) * 1.25);
        ArrayList<Short> tab = new ArrayList<>(size);
        return tab;
    }

}
