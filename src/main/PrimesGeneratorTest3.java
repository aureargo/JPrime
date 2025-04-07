package main;

import jprime.PrimesGeneratorTabMod30030;
import jprime.multithread.PrimesGeneratorTabMod30030MultiThread;

public class PrimesGeneratorTest3 {
    public static void main(String[] args) {
        final long n = 100_000L;
        long start;
        long end;

        start = System.currentTimeMillis();
        long[] tab = PrimesGeneratorTabMod30030.generateTab(n);
        long nbPrimes = PrimesGeneratorTabMod30030.countPrimes(tab, n);
        System.out.println(nbPrimes);
        end = System.currentTimeMillis();
        System.out.println("time : " + (end - start) + " ms");

        System.gc();

        //compare to second generation
        start = System.currentTimeMillis();
        long[] tab2 = PrimesGeneratorTabMod30030MultiThread.generateTab(n);
        long nbPrimes2 = PrimesGeneratorTabMod30030MultiThread.countPrimes(tab2, n);
        System.out.println(nbPrimes2);
        end = System.currentTimeMillis();
        System.out.println("time : " + (end - start) + " ms");

        System.out.println("Nb long in tab : " + tab.length);

        int nbErrors = 0;

        for(int i = 0; i < tab.length; i++) {
            if(tab[i] != tab2[i]) {
                System.out.println("problem at i = " + i + " >> " + Long.toBinaryString(tab[i]) + " / " + Long.toBinaryString(tab2[i]));
//                nbErrors++;
//                if(nbErrors == 20)
//                    break;
            }
        }

        nbPrimes2 = PrimesGeneratorTabMod30030MultiThread.countPrimes(tab2, n);
        System.out.println(nbPrimes2);

        System.out.println("fin");
    }
}
