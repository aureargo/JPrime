/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

import jprime.PrimesGeneratorTabMod30030_old;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrimesGeneratorTabMod30030OldTest
{

    @Test
    void test100()
    {
        long nMax = 100;
        long[] tab = PrimesGeneratorTabMod30030_old.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30030_old.countPrimes(tab, nMax);
        assertEquals(25, nbPrimes);
    }

    @Test
    void test1_000()
    {
        long nMax = 1_000;
        long[] tab = PrimesGeneratorTabMod30030_old.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30030_old.countPrimes(tab, nMax);
        assertEquals(168, nbPrimes);
    }

    @Test
    void test10_000()
    {
        long nMax = 10_000;
        long[] tab = PrimesGeneratorTabMod30030_old.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30030_old.countPrimes(tab, nMax);
        assertEquals(1229, nbPrimes);
    }

    @Test
    void test100_000()
    {
        long nMax = 100_000;
        long[] tab = PrimesGeneratorTabMod30030_old.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30030_old.countPrimes(tab, nMax);
        assertEquals(9592, nbPrimes);
    }

    @Test
    void test1_000_000()
    {
        long nMax = 1_000_000;
        long[] tab = PrimesGeneratorTabMod30030_old.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30030_old.countPrimes(tab, nMax);
        assertEquals(78498, nbPrimes);
    }

    @Test
    void test10_000_000()
    {
        long nMax = 10_000_000;
        long[] tab = PrimesGeneratorTabMod30030_old.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30030_old.countPrimes(tab, nMax);
        assertEquals(664579, nbPrimes);
    }

    @Test
    void test100_000_000()
    {
        long nMax = 100_000_000;
        long[] tab = PrimesGeneratorTabMod30030_old.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30030_old.countPrimes(tab, nMax);
        assertEquals(5761455, nbPrimes);
    }

    @Test
    void test1_000_000_000()
    {
        long nMax = 1_000_000_000;
        long[] tab = PrimesGeneratorTabMod30030_old.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30030_old.countPrimes(tab, nMax);
        assertEquals(50847534, nbPrimes);
    }

    @Test
    void testMaxInteger()
    {
        long nMax = Integer.MAX_VALUE;
        long[] tab = PrimesGeneratorTabMod30030_old.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30030_old.countPrimes(tab, nMax);
        assertEquals(105097565, nbPrimes);
    }

    @Test
    void test10_000_000_000()
    {
        long nMax = 10_000_000_000L;
        long[] tab = PrimesGeneratorTabMod30030_old.generateTab(nMax);
        long nbPrimes = PrimesGeneratorTabMod30030_old.countPrimes(tab, nMax);
        assertEquals(455052511, nbPrimes);
    }
//    @BeforeAll
//    public static void setUpClass()
//    {
//    }
//
//    @AfterAll
//    public static void tearDownClass()
//    {
//    }

//    @BeforeEach
//    public void setUp()
//    {
//    }
//
//    @AfterEach
//    public void tearDown()
//    {
//    }
}
