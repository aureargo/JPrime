/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import java.util.ArrayList;
import jprime.PrimesGeneratorTabEven64Bits;
import jprime.PrimesGeneratorTabEven8Bits;

public class PrimesGeneratorTest
{

    public static void main(String[] args)
    {
        final long n = Integer.MAX_VALUE;
        long start;
        long end;

        start = System.currentTimeMillis();
        long[] tab = PrimesGeneratorTabEven64Bits.generateTab(n);
        long nbPrimes = PrimesGeneratorTabEven64Bits.countPrimes(tab, (int) n);
        System.out.println(nbPrimes);
        end = System.currentTimeMillis();
        System.out.println("time : " + (end - start) + " ms");

        ArrayList<Long> listPrimes = PrimesGeneratorTabEven64Bits.getListPrimes(tab, n);

        tab = null;
        System.gc();

        //compare to second generation
        start = System.currentTimeMillis();
        byte[] tab2 = PrimesGeneratorTabEven8Bits.generateTab(n);
        long nbPrimes2 = PrimesGeneratorTabEven8Bits.countPrimes(tab2, n);
        System.out.println(nbPrimes2);
        end = System.currentTimeMillis();
        System.out.println("time : " + (end - start) + " ms");

        ArrayList<Long> listPrimes2 = PrimesGeneratorTabEven8Bits.getListPrimes(tab2, n);

        tab2 = null;

        int i;
        for (i = 0; i < Math.min(listPrimes.size(), listPrimes2.size()); i++)
        {
            if (listPrimes.get(i).longValue() != listPrimes2.get(i).longValue())
            {
                System.out.println("Problème at index " + i + " ---> " + listPrimes.get(i) + " <-> " + listPrimes2.get(i));
                System.out.println(listPrimes.subList(i, listPrimes.size()));
                System.out.println(listPrimes2.subList(i, listPrimes2.size()));
                break;
            }
        }
        if (listPrimes.size() != listPrimes2.size() && i == Math.min(listPrimes.size(), listPrimes2.size()))
        {
            System.out.println(listPrimes.subList(i, listPrimes.size()));
            System.out.println(listPrimes2.subList(i, listPrimes2.size()));
        }
    }
}
