/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

import jprime.PrimesGeneratorTabMod30030;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrimesGeneratorTabMod30030Test
{
    //10_000              0 ms       //1229
    //100_000             12 ms       //9592
    //1_000_000           20 ms       //78498
    //10_000_000          46 ms       //664579
    //100_000_000         145 ms      //5761455
    //1000_000_000:       3071 ms     //50847534
    //2000_000_000:       ms     //98222287
    //Integer.MAX_VALUE   6855 ms     //105_097_565
    //10_000_000_000L     37976 ms    //455_052_511
    //32_000_000_000L     196568 ms   //1_382_799_415
    //64_000_000_000L     ms   //2_685_000_601
    //100_000_000_000L    ms   //4_118_054_813
    //128_000_000_000L    ms   //5_217_961_818
    //170_000_000_000L    ms  //6_850_708_337
    //171_000_000_000L    ms  //6_889_379_981
    //171_100_000_000L    ms  //6_893_247_776
    //171_150_000_000L    ms  //6_895_180_865

    @Test
    void test100()
    {
        long nMax = 100;
        long[] tab = PrimesGeneratorTabMod30030.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30030.countPrimes(tab, nMax);
        assertEquals(25, nbPrimes);
    }

    @Test
    void test1_000()
    {
        long nMax = 1_000;
        long[] tab = PrimesGeneratorTabMod30030.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30030.countPrimes(tab, nMax);
        assertEquals(168, nbPrimes);
    }

    @Test
    void test10_000()
    {
        long nMax = 10_000;
        long[] tab = PrimesGeneratorTabMod30030.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30030.countPrimes(tab, nMax);
        assertEquals(1229, nbPrimes);
    }

    @Test
    void test100_000()
    {
        long nMax = 100_000;
        long[] tab = PrimesGeneratorTabMod30030.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30030.countPrimes(tab, nMax);
        assertEquals(9592, nbPrimes);
    }

    @Test
    void test1_000_000()
    {
        long nMax = 1_000_000;
        long[] tab = PrimesGeneratorTabMod30030.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30030.countPrimes(tab, nMax);
        assertEquals(78498, nbPrimes);
    }

    @Test
    void test10_000_000()
    {
        long nMax = 10_000_000;
        long[] tab = PrimesGeneratorTabMod30030.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30030.countPrimes(tab, nMax);
        assertEquals(664579, nbPrimes);
    }

    @Test
    void test100_000_000()
    {
        long nMax = 100_000_000;
        long[] tab = PrimesGeneratorTabMod30030.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30030.countPrimes(tab, nMax);
        assertEquals(5761455, nbPrimes);
    }

    @Test
    void test1_000_000_000()
    {
        long nMax = 1_000_000_000;
        long[] tab = PrimesGeneratorTabMod30030.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30030.countPrimes(tab, nMax);
        assertEquals(50847534, nbPrimes);
    }

    @Test
    void testMaxInteger()
    {
        long nMax = Integer.MAX_VALUE;
        long[] tab = PrimesGeneratorTabMod30030.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30030.countPrimes(tab, nMax);
        assertEquals(105097565, nbPrimes);
    }

    @Test
    void test10_000_000_000()
    {
        long nMax = 10_000_000_000L;
        long[] tab = PrimesGeneratorTabMod30030.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30030.countPrimes(tab, nMax);
        assertEquals(455052511, nbPrimes);
    }
}
