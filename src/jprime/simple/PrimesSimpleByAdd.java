/*
 * Copyright (C) août 2020
 * Thales LAS France SAS - all rights reserved
 */
package jprime.simple;

import java.math.BigInteger;
import java.util.ArrayList;

public class PrimesSimpleByAdd
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        compute(500000);
    }

    private static void compute(final long n)
    {
        int sqrtN = (int) Math.sqrt(n);
        ArrayList<Long> tab = new ArrayList(sqrtN);

        long lastX = 0;

        if (n >= 2)
        {
            tab.add((long) 2);
            if (n >= 3)
            {
                tab.add((long) 3);

                for (long x = 5; x <= n; x += 2)
                {
                    boolean findDiv = false;
                    int i = 1;
                    int sqrtX = (int) Math.sqrt(x);
                    long div = 0;

                    while (div <= sqrtX && i < tab.size() && !findDiv)
                    {
                        div = tab.get(i);
                        long mod = x % div;
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


                    int size = tab.size();
                    if (size % 100000 == 0 && !findDiv)
                    {
                        float percent = x * 100.0f / n;
                        System.out.println(percent + "%\t" + " => " + size + " elements \t" + x + " (+" + (x - lastX) + ")");
                        lastX = x;
                    }

                    if (x == Long.MAX_VALUE)
                    {
                        break;
                    }
                }
            }

            StringBuilder stringBuilder = new StringBuilder(BLOCK * 4);

            int size = tab.size();
            System.out.println("nb of primary numbers = " + size);
//            int nbBlock = size / BLOCK;
//            int reste = size % BLOCK;
            int i = 0;
            long max = BLOCK;
            int lastI = 0;
            while (i < size)
            {
                long val = tab.get(i);
                stringBuilder.append(val);
                i++;
                while (i < size)
                {
                    val = tab.get(i);
                    if (val < max)
                    {
                        stringBuilder.append(';').append(val);
                        i++;
                    }
                    else
                    {
                        break;
                    }
                }
                System.out.println((i - lastI) + " => [" + stringBuilder.toString() + ']');
                stringBuilder.delete(0, Integer.MAX_VALUE);
                max += BLOCK;
                lastI = i;
            }

        }

//            System.out.println(size + " => [" + stringBuilder.toString() + ']');
    }
    private static final int BLOCK = 5000;

    public static BigInteger sqrt(BigInteger x)
    {
        BigInteger div = BigInteger.ZERO.setBit(x.bitLength() / 2);
        BigInteger div2 = div;
        // Loop until we hit the same value twice in a row, or wind
        // up alternating.
        for (;;)
        {
            BigInteger y = div.add(x.divide(div)).shiftRight(1);
            if (y.equals(div) || y.equals(div2))
            {
                return y;
            }
            div2 = div;
            div = y;
        }
    }

    public static ArrayList<Integer> allocListInFoncOfN(int n)
    {
        //theoreme de fonction de compte des nombres premiers de Legendre-Gauss
        int size = (int) Math.ceil((n / Math.log(n)) * 1.25);
        ArrayList<Integer> tab = new ArrayList<>(size);
        return tab;
    }

    public static ArrayList<Long> allocListInFoncOfN(long n)
    {
        //theoreme de fonction de compte des nombres premiers de Legendre-Gauss
        int size = (int) Math.ceil((n / Math.log(n)) * 1.25);
        ArrayList<Long> tab = new ArrayList<>(size);
        return tab;
    }
}
