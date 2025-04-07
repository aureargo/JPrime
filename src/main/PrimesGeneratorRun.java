/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import jprime.PrimesGeneratorTabMod30030;

public class PrimesGeneratorRun
{

    public static void main(String[] args)
    {
        final long nMax = 1_000_000_000L;

        long start = System.currentTimeMillis();
        long[] tab = PrimesGeneratorTabMod30030.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30030.countPrimes(tab, nMax);
        long end = System.currentTimeMillis();

        System.out.println(nbPrimes);
        System.out.println("time : " + (end - start) + " ms");
    }

}
