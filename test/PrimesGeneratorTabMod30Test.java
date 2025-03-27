/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

import jprime.PrimesGeneratorTabMod30;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrimesGeneratorTabMod30Test
{
    //10_000             8 ms        //1229
    //100_000            9 ms        //9592
    //1_000_000          15 ms       //78498
    //10_000_000         30 ms       //664579
    //100_000_000        181 ms      //5761455
    //1000_000_000:      3067 ms     //50847534
    //2000_000_000:      6596 ms     //98222287
    //Integer.MAX_VALUE: 7141 ms    //105097565
    //10_000_000_000L    37631 ms    //455052511
    //32_000_000_000L    141249 ms   //1_382_799_415
    //64_000_000_000L     ms   //2_685_000_601
    //100_000_000_000L    ms   //4_118_054_813
    //128_000_000_000L    gms   //5_217_961_818
    //170_000_000_000L    ms  //6_850_708_337
    //171_000_000_000L    ms  //6_889_379_981
    //171_100_000_000L    ms  //6_893_247_776
    //171_150_000_000L    ms  //6_895_180_865

    @Test
    void test100()
    {
        long nMax = 100;
        byte[] tab = PrimesGeneratorTabMod30.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30.countPrimes(tab, nMax);
        assertEquals(25, nbPrimes);
    }

    @Test
    void test1_000()
    {
        long nMax = 1_000;
        byte[] tab = PrimesGeneratorTabMod30.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30.countPrimes(tab, nMax);
        assertEquals(168, nbPrimes);
    }

    @Test
    void test10_000()
    {
        long nMax = 10_000;
        byte[] tab = PrimesGeneratorTabMod30.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30.countPrimes(tab, nMax);
        assertEquals(1229, nbPrimes);
    }

    @Test
    void test100_000()
    {
        long nMax = 100_000;
        byte[] tab = PrimesGeneratorTabMod30.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30.countPrimes(tab, nMax);
        assertEquals(9592, nbPrimes);
    }

    @Test
    void test1_000_000()
    {
        long nMax = 1_000_000;
        byte[] tab = PrimesGeneratorTabMod30.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30.countPrimes(tab, nMax);
        assertEquals(78498, nbPrimes);
    }

    @Test
    void test10_000_000()
    {
        long nMax = 10_000_000;
        byte[] tab = PrimesGeneratorTabMod30.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30.countPrimes(tab, nMax);
        assertEquals(664579, nbPrimes);
    }

    @Test
    void test100_000_000()
    {
        long nMax = 100_000_000;
        byte[] tab = PrimesGeneratorTabMod30.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30.countPrimes(tab, nMax);
        assertEquals(5761455, nbPrimes);
    }

    @Test
    void test1_000_000_000()
    {
        long nMax = 1_000_000_000;
        byte[] tab = PrimesGeneratorTabMod30.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30.countPrimes(tab, nMax);
        assertEquals(50847534, nbPrimes);
    }

    @Test
    void testMaxInteger()
    {
        long nMax = Integer.MAX_VALUE;
        byte[] tab = PrimesGeneratorTabMod30.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30.countPrimes(tab, nMax);
        assertEquals(105097565, nbPrimes);
    }

    @Test
    void test10_000_000_000()
    {
        long nMax = 10_000_000_000L;
        byte[] tab = PrimesGeneratorTabMod30.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30.countPrimes(tab, nMax);
        assertEquals(455052511, nbPrimes);
    }
}
