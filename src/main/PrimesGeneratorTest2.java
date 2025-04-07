package main;

import jprime.PrimesGeneratorTabMod30030;
import jprime.PrimesGeneratorTabMod30030_old;

public class PrimesGeneratorTest2 {
    public static void main(String[] args) {
        final long n = 10_000_000L;
        long start;
        long end;

        start = System.currentTimeMillis();
        long[] tab = PrimesGeneratorTabMod30030_old.generateTab(n);
        long nbPrimes = PrimesGeneratorTabMod30030_old.countPrimes(tab, n);
        System.out.println(nbPrimes);
        end = System.currentTimeMillis();
        System.out.println("time : " + (end - start) + " ms");

        System.gc();

        //compare to second generation
        start = System.currentTimeMillis();
        long[] tab2 = PrimesGeneratorTabMod30030.generateTab(n);
        long nbPrimes2 = PrimesGeneratorTabMod30030.countPrimes(tab2, n);
        System.out.println(nbPrimes2);
        end = System.currentTimeMillis();
        System.out.println("time : " + (end - start) + " ms");

        System.out.println("Nb long in tab : " + tab.length);

//        int nbErrors = 0;

        for(int i = 0; i < tab.length; i++) {
            if(inverseBits(tab[i]) != tab2[i]) {
                System.out.println("problem at i = " + i + " >> " + Long.toBinaryString(tab[i]) + " / " + new StringBuilder(Long.toBinaryString(tab2[i])).reverse().toString());
//                nbErrors++;
//                if(nbErrors == 20)
//                    break;
            }
        }
    }

    private static long inverseBits(long v)
    {
        long res = 0;
        for(int shift = 0; shift < 64; shift++) {
            res |= (v & (0b1 << shift)) << (63-shift);
        }
        return res;
    }
}
