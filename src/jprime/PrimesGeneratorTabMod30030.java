/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jprime;

import java.util.ArrayList;

/**
 * 30030n+1 //(1), 30031, 60061, 90091, 120121
 * 30030n+17 // 17, 30047, 60077, 90107, 120137
 * 30030n+19 // 19, 30049, 60079, 90109, 120139
 * 30030n+23 // 23, 30053, 60083, 90113, 120143
 * 30030n+29 // 29, 30059, 60089, 90119, 120149
 * 30030n+31 // 31, 30061, 60091, 90121, 120151
 * ...
 *
 * 2*3*5*7*11*13 = 30300 on 5760 bits, 720 bytes (90 longs)
 *
 * Evolution: merge of first loop (1 to 30029) with fill of cacheBitShift and cacheMultDiv.
 */
public class PrimesGeneratorTabMod30030
{

    private static final int NB_IN_BLOCK = 2 * 3 * 5 * 7 * 11 * 13;   //30030
    private static final int NB_BITS = 5760;
    private static final int NB_BYTES = NB_BITS / 8;  //720
    private static final int NB_LONG = NB_BYTES / 8;  //90
    private static final short[] TAB_ELTS = new short[NB_BITS]; //[1,17,19,23,29,31, ... 30013,30029]
    private static final long BIT_63 = 0x80_00_00_00_00_00_00_00L;

    static
    {
        TAB_ELTS[0] = 1;
        int i = 1;
        for (short x = 17; x < NB_IN_BLOCK; x += 2)
        {
            if (x % 3 == 0 || x % 5 == 0 || x % 7 == 0 || x % 11 == 0 || x % 13 == 0)
            {
                continue;
            }
            TAB_ELTS[i++] = x;
        }
    }
    private static final short LAST_ELT = TAB_ELTS[NB_BITS - 1];    //30029

    //10_000             3 ms        //1229
    //100_000            11 ms       //9592
    //1_000_000          18 ms       //78498
    //10_000_000         45 ms       //664579
    //100_000_000        148 ms      //5761455
    //1000_000_000:      2357 ms     //50847534
    //2000_000_000:      5199 ms     //98222287
    //Integer.MAX_VALUE  5733 ms     //105_097_565
    //10_000_000_000L    35028 ms    //455_052_511
    //32_000_000_000L    135568 ms   //1_382_799_415
    //64_000_000_000L    325920 ms   //2_685_000_601
    //100_000_000_000L   613898 ms   //4_118_054_813
    //128_000_000_000L   861141 ms   //5_217_961_818
    //170_000_000_000L   1261992 ms  //6_850_708_337
    //171_000_000_000L   1324808 ms  //6_889_379_981
    //171_100_000_000L   1310419 ms  //6_893_247_776
    //171_150_000_000L   1299927 ms  //6_895_180_865
    /**
     *
     * @param nMax [0..170_000_000_000L]
     * @return nb nombres premiers <= nMax
     */
    public static long[] generateTab(long nMax)
    {
        long start = System.currentTimeMillis();
        long end;

        final int nbBlocks = (int) (((nMax - 1) / NB_IN_BLOCK) + 1);   //for 100_000, we need 4 blocks of 30030,
        final int size = nbBlocks * NB_LONG;                        // with 90 longs each = 360 longs for 4 blocks

        long[] tab = new long[size];

        end = System.currentTimeMillis();
        System.out.println("allocation tab execution time = " + (end - start) + " ms");
        start = end;

        tab[0] = BIT_63;   //only 1 is not prime (PS: not in 30030. 17*17,17*19, 19*19 ...97*293 ...)

        if(nMax < 30031*17) {
            loop1To30029WithoutCache(tab, nMax, size);
        }
        else {
            //tab for cache
            short[][] cacheTabs = createCacheTabs(nMax);
            short[] cacheMultDiv30030 = cacheTabs[0];
            short[] cacheBitShift = cacheTabs[1];

            end = System.currentTimeMillis();
            System.out.println("allocation cache execution time = " + (end - start) + " ms");
            start = end;

            loop1To30029(tab, nMax, size, cacheMultDiv30030, cacheBitShift);

            ////////////////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////
            end = System.currentTimeMillis();
            System.out.println("mini loop elimination non-primes execution time = " + (end - start) + " ms");
            start = end;
            ////////////////////////////////////////////////////////////////////////
            //////////////////////////////Second loop///////////////////////////////

            loop30030ToMax(tab, nMax, size, nbBlocks, cacheMultDiv30030, cacheBitShift);
        }

        end = System.currentTimeMillis();
        System.out.println("mega loop elimination non-primes execution time = " + (end - start) + " ms");

        return tab;
    }

    private static short[][] createCacheTabs(final long nMax)
    {
        int sizeCacheMultDiv = ((NB_BITS - 2) * (NB_BITS - 1)) / 2; // (5758*5759)/2 = 16_580_161
        int sizeCacheBitShift = ((NB_BITS - 2) / 2) * ((NB_BITS - 2) / 2 + 1);  // for 8*8, we have 3*4=12, and for 5758, we have 2879*2880 = 8_291_520

        //tab for cache
        final short[] cacheMultDiv30030 = new short[sizeCacheMultDiv];
        final short[] cacheBitShift = new short[sizeCacheBitShift]; // for 8*8, we have 3*4=12, and for 5758, we have 2879*2880 = 8_291_520
        final short[][] cacheTabs =
        {
            cacheMultDiv30030, cacheBitShift
        };
        //cacheBitShift
        //[   1,    2,    4,    8,   16,   32,   64, -128]  =>  == 0b1L << jb
        //[   2,   32,   16,    1, -128,    8,    4,   64]  =>        [   32,   16,    1, -128,    8,    4]
        //[   4,   16,    1,   64,    2, -128,    8,   32]  =>              [    1,   64,    2, -128]
        //[   8,    1,   64,   32,    4,    2, -128,   16]  =>                    [   32,    4]
        //[  16, -128,    2,    4,   32,   64,    1,    8]  =>  
        //[  32,    8, -128,    2,   64,    1,   16,    4]  =>  
        //[  64,    4,    8, -128,    1,   16,   32,    2]  =>  
        //[-128,   64,   32,   16,    8,    4,    2,    1]  => 
        //
        //cacheMultDiv30
        //[0, 0,  0,  0,  0,  0,  0,  0]    => == 0
        //[0, 1,  2,  3,  3,  4,  5,  6]    =>  [  1,  2,  3,  3,  4,  5]
        //[0, 2,  4,  4,  6,  6,  8, 10]    =>      [  4,  4,  6,  6,  8]
        //[0, 3,  4,  5,  7,  8,  9, 12]    =>          [  5,  7,  8,  9]
        //[0, 3,  6,  7,  9, 10, 13, 16]    =>              [  9, 10, 13]
        //[0, 4,  6,  8, 10, 12, 14, 18]    =>                  [ 12, 14]
        //[0, 5,  8,  9, 13, 14, 17, 22]    =>                      [ 17]
        //[0, 6, 10, 12, 16, 18, 22, 28]    => == els[jb]-1
//        
//            System.out.println(Arrays.toString(cacheMask[));
//            System.out.println(Arrays.toString(cacheMultDiv30));
        return cacheTabs;
    }

    private static void loop1To30029WithoutCache(final long[] tab, final long nMax, final int size)
    {
        short[] tabIndexBitShift = new short[NB_IN_BLOCK];    //96ms
        for (short i = 0; i < NB_BITS && TAB_ELTS[i] <= nMax; i++)
        {
            tabIndexBitShift[TAB_ELTS[i]] = i;
        }

        final int sqrtN = (int) Math.sqrt(nMax);

        for (int ib = 1; ib < NB_BITS; ib++)
        {
            final int v1 = TAB_ELTS[ib];
            if (v1 > sqrtN)
            {
                break;
            }
            final int iLongV1 = ib >>> 6; // /64
            final int iShiftV1 = ib & 0b11_1111;  //%64
            final long bitsAnd1 = BIT_63 >>> iShiftV1;
            final boolean isV1Prime = (tab[iLongV1] & bitsAnd1) == 0b0;
            final int v1Mult = v1 * NB_LONG;

            //no loop 1.1: 1 * (30030k+1)->(30030k+30029) //we don't want multiply by 1 to set if a number is prime
            if (isV1Prime)
            {
                //loop 1.2: (30030k+1) * 1->30029
                for (int iTab = v1Mult + iLongV1; iTab < size; iTab += v1Mult)    //els[ib]*(30030k+1) with k>=1 --> 17*30031, 17*60061, 17*90091, 17*120121
                {
                    tab[iTab] |= bitsAnd1;
                    //(17*1     =      17 =>      17%30030=17    =>      17/30030= 0)
                    // 17*30031 =  510527 =>  510527%30030=17    =>  510527/30030=17
                    // 17*60061 = 1021037 => 1021037%30030=17    => 1021037/30030=34
                    //=> 17*(30030k+1) %30030 don't change with +30030 in multiplier, and +17 in tab index for each +30030
                }
            }

            //els[ib]*(30030k+els[jb]) with k>=0 and x>=1 --> 17*17, 17*30047, 17*60077...17*19, 17*30049, 17*60079...17*30029, 17*60059...
            for (int jb = ib; jb < NB_BITS - 1; jb++)    //els[ib]*(30k+els[jb]) with k>=0 and x>=1 --> 7*7, 7*37, 7*67...7*11, 7*41, 7*71...7*29, 7*59...
            {
                final int v2 = TAB_ELTS[jb];
                final int v = v1 * v2;
                final short iBlock = (short) (v / NB_IN_BLOCK);

                final short bitShift;
                // => 901_380_529%30030 = 289 => bit 57 (56)
                final short modulo = (short) (v - NB_IN_BLOCK * iBlock);
//                    int ibShift = Arrays.binarySearch(TAB_ELTS, mod);   //Too Slow (500 ms)
                bitShift = tabIndexBitShift[modulo];//mapModToBitShift.get(new Short2(modulo));


                final int iLong17to30013 = bitShift >>> 6; // /64
                final int iShift17to30013 = bitShift & 0b11_1111;  //%64
                final long bitsAnd17to30013 = BIT_63 >>> iShift17to30013;
                final int iTabInit = iBlock * NB_LONG + iLong17to30013;    //getCacheMultDiv30(cacheMultDiv30, ib, jb);
                if (isV1Prime)
                {
                    //loop 1.3: 17->30013 * (17->30013 + 30030k)
                    for (int iTab = iTabInit; iTab < size; iTab += v1Mult)
                    {
                        tab[iTab] |= bitsAnd17to30013;
                        //17*19    =     323 =>     323%30030=323    =>     323/30030=0
                        //17*30049 =  510833 =>  510833%30030=323    =>  510833/30030=17
                        //17*60079 = 1021343 => 1021343%30030=323    => 1021343/30030=34
                        //=> 17*(30030k+19) %30030 don't change with +30030 in multiplier, and +17 in tab index for each +30 starting by (17*19)/30030 = 0
                    }
                }
                if (ib != jb)
                {
                    final int iLongV2 = jb >>> 6; // /64
                    final int iShiftV2 = jb & 0b11_1111;  //%64
                    final long bitsAndV2 = BIT_63 >>> iShiftV2;
                    final boolean isV2Prime = (tab[iLongV2] & bitsAndV2) == 0b0;
                    if (isV2Prime)
                    {
                        //loop 1.4: (17->30013 + 30030k) * 19->30013
                        final int v2Mult = v2 * NB_LONG;
                        for (int iTab = iTabInit + v2Mult; iTab < size; iTab += v2Mult)
                        {
                            tab[iTab] |= bitsAnd17to30013;
                            //(   17*19 =     323 =>     323%30030=323    =>     323/30030= 0)
                            // 30047*19 =  570893 =>  570893%30030=323    =>  570893/30030=19   (+19)
                            // 60077*19 = 1141463 => 1141463%30030=323    => 1141463/30030=38   (+19)
                            //=> (30k+17)*19 %30030 don't change with +30030 in multiplier, and +19 in tab index for each +30030 starting by (17*19)/30030 = 0+19
                        }
                    }
                }
            }

            final int bitShift = (NB_BITS - 1 - ib);
            final int iLong30029 = bitShift >>> 6; // /64
            final int iShift30029 = bitShift & 0b11_1111;  //%64
            final long bitsAnd30029 = BIT_63 >>> iShift30029;
            final int iTabInit = (v1 - 1) * NB_LONG + iLong30029;
            if (isV1Prime)
            {
                //loop 1.5: 17->30029 * (30030k+30029)
                for (int iTab = iTabInit; iTab < size; iTab += v1Mult)
                {
                    tab[iTab] |= bitsAnd30029;
                    //  17*30029 =     510493 =>   510493%30030=30013      510493/30030=16  = 17-1
                    //  17*60059 =    1021003 =>  1021003%30030=30013     1021003/30030=33  (+17)
                    //  17*90089 =    1531513 =>  1531513%30030=30013     1531513/30030=50  (+17)
                }
            }
            if (ib != NB_BITS - 1)  //30029 is prime
            {
                //loop 1.6: (30030k + 17->30013) * 30029
                final int v2 = LAST_ELT;  // == 30029
                final int v2Mult = v2 * NB_LONG;
                for (int iTab = iTabInit + v2Mult; iTab < size; iTab += v2Mult)
                {
                    tab[iTab] |= bitsAnd30029;
                    //(   17*30029 =     510493 =>     510493%30030=30013        510493/30030=16     = 17-1)
                    // 30047*30029 =  902281363 =>  902281363%30030=30013     902281363/30030=30045  (+30029)
                    // 60077*30029 = 1804052233 => 1804052233%30030=30013    1804052233/30030=60074  (+30029)
                }
            }
        }
    }

    private static void loop1To30029(final long[] tab, final long nMax, final int size, final short[] cacheMultDiv30030, final short[] cacheBitShift)
    {
        //        HashMap<Short2, Short> mapModToBitShift = new HashMap<>(NB_BITS);    //Short2 and NB_IN_BLOCK or NB_BITS: 225ms     Short and NB_BITS: 314ms    Short and NB_IN_BLOCK: 310ms
        short[] tabIndexBitShift = new short[NB_IN_BLOCK];    //96ms
        for (short i = 0; i < NB_BITS && TAB_ELTS[i] <= nMax; i++)
        {
            tabIndexBitShift[TAB_ELTS[i]] = i;
        }

        final int sqrtN = (int) Math.sqrt(nMax);

        int iMask = 0;
        int iMultDiv30030 = 0;
        for (int ib = 1; ib < NB_BITS; ib++)
        {
            final int v1 = TAB_ELTS[ib];
            if (v1 > sqrtN)
            {
                break;
            }
            final int iLongV1 = ib >>> 6; // /64
            final int iShiftV1 = ib & 0b11_1111;  //%64
            final long bitsAnd1 = BIT_63 >>> iShiftV1;
            final boolean isV1Prime = (tab[iLongV1] & bitsAnd1) == 0b0;
            final int v1Mult = v1 * NB_LONG;

            //no loop 1.1: 1 * (30030k+1)->(30030k+30029) //we don't want multiply by 1 to set if a number is prime
            if (isV1Prime)
            {
                //loop 1.2: (30030k+1) * 1->30029
                for (int iTab = v1Mult + iLongV1; iTab < size; iTab += v1Mult)    //els[ib]*(30030k+1) with k>=1 --> 17*30031, 17*60061, 17*90091, 17*120121
                {
                    tab[iTab] |= bitsAnd1;
                    //(17*1     =      17 =>      17%30030=17    =>      17/30030= 0)
                    // 17*30031 =  510527 =>  510527%30030=17    =>  510527/30030=17
                    // 17*60061 = 1021037 => 1021037%30030=17    => 1021037/30030=34
                    //=> 17*(30030k+1) %30030 don't change with +30030 in multiplier, and +17 in tab index for each +30030 
                }
            }

            //els[ib]*(30030k+els[jb]) with k>=0 and x>=1 --> 17*17, 17*30047, 17*60077...17*19, 17*30049, 17*60079...17*30029, 17*60059...
            for (int jb = ib; jb < NB_BITS - 1; jb++)    //els[ib]*(30k+els[jb]) with k>=0 and x>=1 --> 7*7, 7*37, 7*67...7*11, 7*41, 7*71...7*29, 7*59...
            {
                final int v2 = TAB_ELTS[jb];
                final int v = v1 * v2;
                final short iBlock = (short) (v / NB_IN_BLOCK);
                cacheMultDiv30030[iMultDiv30030++] = iBlock;

                final short bitShift;
                if (ib + jb < NB_BITS)
                {
                    // => 901_380_529%30030 = 289 => bit 57 (56)
                    final short modulo = (short) (v - NB_IN_BLOCK * iBlock);
//                    int ibShift = Arrays.binarySearch(TAB_ELTS, mod);   //Too Slow (500 ms)
                    bitShift = tabIndexBitShift[modulo];//mapModToBitShift.get(new Short2(modulo));
                    cacheBitShift[iMask++] = bitShift;
                }
                else
                {
                    bitShift = getCacheBitShift(cacheBitShift, ib, jb);
                }

                final int iLong17to30013 = bitShift >>> 6; // /64
                final int iShift17to30013 = bitShift & 0b11_1111;  //%64
                final long bitsAnd17to30013 = BIT_63 >>> iShift17to30013;
                final int iTabInit = iBlock * NB_LONG + iLong17to30013;    //getCacheMultDiv30(cacheMultDiv30, ib, jb);
                if (isV1Prime)
                {
                    //loop 1.3: 17->30013 * (17->30013 + 30030k)
                    for (int iTab = iTabInit; iTab < size; iTab += v1Mult)
                    {
                        tab[iTab] |= bitsAnd17to30013;
                        //17*19    =     323 =>     323%30030=323    =>     323/30030=0
                        //17*30049 =  510833 =>  510833%30030=323    =>  510833/30030=17
                        //17*60079 = 1021343 => 1021343%30030=323    => 1021343/30030=34
                        //=> 17*(30030k+19) %30030 don't change with +30030 in multiplier, and +17 in tab index for each +30 starting by (17*19)/30030 = 0
                    }
                }
                if (ib != jb)
                {
                    final int iLongV2 = jb >>> 6; // /64
                    final int iShiftV2 = jb & 0b11_1111;  //%64
                    final long bitsAndV2 = BIT_63 >>> iShiftV2;
                    final boolean isV2Prime = (tab[iLongV2] & bitsAndV2) == 0b0;
                    if (isV2Prime)
                    {
                        //loop 1.4: (17->30013 + 30030k) * 19->30013
                        final int v2Mult = v2 * NB_LONG;
                        for (int iTab = iTabInit + v2Mult; iTab < size; iTab += v2Mult)
                        {
                            tab[iTab] |= bitsAnd17to30013;
                            //(   17*19 =     323 =>     323%30030=323    =>     323/30030= 0)
                            // 30047*19 =  570893 =>  570893%30030=323    =>  570893/30030=19   (+19)
                            // 60077*19 = 1141463 => 1141463%30030=323    => 1141463/30030=38   (+19)
                            //=> (30k+17)*19 %30030 don't change with +30030 in multiplier, and +19 in tab index for each +30030 starting by (17*19)/30030 = 0+19
                        }
                    }
                }
            }

            final int bitShift = (NB_BITS - 1 - ib);
            final int iLong30029 = bitShift >>> 6; // /64
            final int iShift30029 = bitShift & 0b11_1111;  //%64
            final long bitsAnd30029 = BIT_63 >>> iShift30029;
            final int iTabInit = (v1 - 1) * NB_LONG + iLong30029;
            if (isV1Prime)
            {
                //loop 1.5: 17->30029 * (30030k+30029)
                for (int iTab = iTabInit; iTab < size; iTab += v1Mult)
                {
                    tab[iTab] |= bitsAnd30029;
                    //  17*30029 =     510493 =>   510493%30030=30013      510493/30030=16  = 17-1
                    //  17*60059 =    1021003 =>  1021003%30030=30013     1021003/30030=33  (+17)  
                    //  17*90089 =    1531513 =>  1531513%30030=30013     1531513/30030=50  (+17)
                }
            }
            if (ib != NB_BITS - 1)  //30029 is prime
            {
                //loop 1.6: (30030k + 17->30013) * 30029
                final int v2 = LAST_ELT;  // == 30029
                final int v2Mult = v2 * NB_LONG;
                for (int iTab = iTabInit + v2Mult; iTab < size; iTab += v2Mult)
                {
                    tab[iTab] |= bitsAnd30029;
                    //(   17*30029 =     510493 =>     510493%30030=30013        510493/30030=16     = 17-1)
                    // 30047*30029 =  902281363 =>  902281363%30030=30013     902281363/30030=30045  (+30029)
                    // 60077*30029 = 1804052233 => 1804052233%30030=30013    1804052233/30030=60074  (+30029)
                }
            }
        }
    }

    private static void loop30030ToMax(final long[] tab, final long nMax,
            final int size, final int nbBlocks,
            final short[] cacheMultDiv30030, final short[] cacheBitShift)
    {
        final int sqrtN = (int) Math.sqrt(nMax);
        int mult30030 = NB_IN_BLOCK;

        outerloop:
        for (int iBlock = 1; iBlock < nbBlocks; iBlock++)
        {
            if (mult30030 + 1 > sqrtN)
            {
                break;// outerloop;
            }

            final int i = iBlock * NB_LONG;
            final long bits1 = tab[i];

            ////////////////////////////////////////////////////////////////////
            //30031 60061 90091 120121
            final boolean isV1Prime30031 = (bits1 & BIT_63) == 0b0;
            for (int jb = 0; jb < NB_BITS; jb++)
            {
                final int jLong = jb >>> 6;
                final int jShift = jb & 0b11_1111;
                final long bitsAnd30031With1to30029 = BIT_63 >>> jShift;

                final int v2 = mult30030 + TAB_ELTS[jb];
                final int iTabInit = (v2 + 1) * i + jLong;
                if (isV1Prime30031)
                {
                    //loop 2.1: 30031 * (30030k+1)->(30030k+30029)
                    final int v1Mult30031 = (mult30030 + 1) * NB_LONG;
                    for (int iTab = iTabInit; iTab < size; iTab += v1Mult30031)
                    {
                        tab[iTab] |= bitsAnd30031With1to30029;
                        // 30031*30047 =  902341457 =>  902341457%30030=17    =>  902341457/30030= 30048  = 30047+1
                        // 30031*60077 = 1804172387 => 1804172387%30030=17    => 1804172387/30030= 60079  (+30031)
                        // 30031*90107 = 2706003317 => 2706003317%30030=17    => 2706003317/30030= 90110  (+30031)

                        // 60061* 60077 = 3608284697 => 3608284697%30030=17    => 3608284697/30030=120156  = (60077+1)*2
                        // 60061* 90107 = 5411916527 => 5411916527%30030=17    => 5411916527/30030=180217  (+60061)
                        // 60061*120137 = 7215548357 => 7215548357%30030=17    => 7215548357/30030=240278  (+60061)
                        // 90091* 90107 = 8117829737 => 8117829737%30030=17    => 8117829737/30030=270324  = (90107+1)*3
                    }
                }
                if (jb != 0)
                {
                    final boolean isV2Prime = (tab[i + jLong] & bitsAnd30031With1to30029) == 0b0;
                    if (isV2Prime)
                    {
                        //loop 2.2: (30030k+1) * 30031->60059
                        final int v2Mult = v2 * NB_LONG;
                        for (int iTab = iTabInit + v2Mult; iTab < size; iTab += v2Mult)
                        {
                            tab[iTab] |= bitsAnd30031With1to30029;
                            // 60061*30047 = 1804652867 => 1804652867%30030=17    => 1804652867/30030= 60095   = 30047+1 +30047
                            // 90091*30047 = 2706964277 => 2706964277%30030=17    => 2706964277/30030= 90142   (+30047)

                            //  90091*60077= 5412397007 => 5412397007%30030=17    => 5412397007/30030=180233   = (60077+1)*2 + 60077
                            // 120121*60077= 7216509317 => 7216509317%30030=17    => 7216509317/30030=240310   (+60077)
                        }
                    }
                }
            }

            int iMask = 0;
            int iMultDiv30 = 0;
            for (int ib = 1; ib < NB_BITS; ib++)
            {
                final int v1 = mult30030 + TAB_ELTS[ib];
                if (v1 > sqrtN)
                {
                    break outerloop;
                }

                final int iLong = ib >>> 6; // /64
                final int iShift = ib & 0b11_1111;  //%64
                final long bitsAndV1 = BIT_63 >>> iShift;
                final boolean isV1Prime = (tab[i + iLong] & bitsAndV1) == 0b0;
                final int v1Mult = v1 * NB_LONG;

                for (int jb = ib; jb < NB_BITS - 1; jb++)    //els[ib]*(30k+els[jb]) with k>=0 and x>=1 --> 7*7, 7*37, 7*67...7*11, 7*41, 7*71...7*29, 7*59...
                {
                    final short bitShift = (ib + jb < NB_BITS ? cacheBitShift[iMask++] : getCacheBitShift(cacheBitShift, ib, jb));
                    final int iLong17to30013 = bitShift >>> 6; // /64
                    final int iShift17to30013 = bitShift & 0b11_1111;  //%64
                    final long bitsAnd17to30013 = BIT_63 >>> iShift17to30013;

                    final int iTabInit = ((v1 + TAB_ELTS[jb]) * iBlock + cacheMultDiv30030[iMultDiv30++]) * NB_LONG + iLong17to30013;    //getCacheMultDiv30(cacheMultDiv30, ib, jb);
                    //mod(30): 30047*30053 = (30+7)*(30+13) => (a+b)*(a+c) = a*a + ab + ac + bc => 900 + 30*7 + 30*13 + 7*13 => (mod 30) 0+0+0+ 91%30 = 1
                    //iTab  : (30047*30053)/30030 = (30030*30030 + 30030*17 + 30030*23 + 391)/30030 = 30030+17+23+(391/30030) = 30070
                    //iTab  : (60077*60083)/30030 = (60060*60060 + 60060*17 + 60060*23 + 391)/30030 = 2*60060 + 2*17 + 2*23 + (391/30030) = (60060 + 17 + 23)*2 + (391/30030) = 120200

                    if (isV1Prime)
                    {
                        //loop 2.3: 30047->60043 * 30047->60043
                        for (int iTab = iTabInit; iTab < size; iTab += v1Mult)
                        {
                            tab[iTab] |= bitsAnd17to30013;
                            //30047*30053 =  903002491 =>  903002491%30030=391    =>  903002491/30030= 30070  = 30030+17+23+((17*23)/30030)
                            //30047*60083 = 1805313901 => 1805313901%30030=391    => 1805313901/30030= 60117  (+30047)
                            //30047*90113 = 2707625311 => 2707625311%30030=391    => 2707625311/30030= 90164  (+30047)
                            //=> 30047*(30030k+23) %30030 don't change with +30030 in multiplier, and +30047 in tab index for each +30030 starting by (30047*30053)%30030 = 391
                        }
                    }
                    if (ib != jb)
                    {
                        final int iLongV2 = jb >>> 6; // /64
                        final int iShiftV2 = jb & 0b11_1111;  //%64
                        final long bitsAndV2 = BIT_63 >>> iShiftV2;
                        final boolean isV2Prime = (tab[i + iLongV2] & bitsAndV2) == 0b0;
                        if (isV2Prime)
                        {
                            //loop 2.4: 30049->60043 * 30047->60043
                            final int v2 = mult30030 + TAB_ELTS[jb];
                            final int v2Mult = v2 * NB_LONG;
                            for (int iTab = iTabInit + v2Mult; iTab < size; iTab += v2Mult)
                            {
                                tab[iTab] |= bitsAnd17to30013;
                                //(30047*30053 =  903002491 =>  903002491%30030=391    =>  903002491/30030= 30070)
                                // 60077*30053 = 1805494081 => 1805494081%30030=391    => 1805494081/30030= 60123   (+30053)
                                // 90107*30053 = 2707985671 => 2707985671%30030=391    => 2707985671/30030= 90176   (+30053)
                                //=> (30030k+7)*30043 %30030 don't change with +30030 in multiplier, and +30053 in tab index for each +30030 starting by (30047*30053)%30030 = 391
                            }
                        }
                    }
                }

                final int bitShift = (NB_BITS - 1 - ib);
                final int iLong60059 = bitShift >>> 6; // /64
                final int iShift60059 = bitShift & 0b11_1111;  //%64
                final long bitsAnd60059 = BIT_63 >>> iShift60059;

                final int iTabInit = ((v1 + LAST_ELT) * iBlock + (TAB_ELTS[ib] - 1)) * NB_LONG + iLong60059; //53*59 = 3127 => 3127%30=7 => 3127/30=104 == (53+29)*1 + 22    41*59 = 2419 => 2419%30=19 => 2419/80 = (41*29)*1 + 10
                if (isV1Prime)
                {
                    //loop 2.5: 30031->60059 * (30030k+30029)
                    for (int iTab = iTabInit; iTab < size; iTab += v1Mult)
                    {
                        tab[iTab] |= bitsAnd60059;
                        // 30031* 60059 = 1803631829 => 1803631829%30030=30029    => 1803631829/30030= 60060  = (30029+1)*i
                        // 30031* 90089 = 2705462759 => 2705462759%30030=30029    => 2705462759/30030= 90091  (+30031)
                        // 30031*120119 = 3607293689 => 3607293689%30030=30029    => 3607293689/30030=120122  (+30031)
                    }
                }
                if (ib != NB_BITS - 1)
                {
                    final boolean isV2Prime = (tab[iBlock * (NB_LONG + 1) - 1] & 0b1L) == 0b0;
                    if (isV2Prime)
                    {
                        //loop 2.6: (30030k + 30031->60043) * 60059
                        final int v2 = mult30030 + LAST_ELT;  // == 60059, 90089, 120119 ...
                        final int v2Mult = v2 * NB_LONG;
                        for (int iTab = iTabInit + v2Mult; iTab < size; iTab += v2Mult)
                        {
                            tab[iTab] |= bitsAnd60059;
                            //( 60061*60059 = 3607203599 => 3607203599%30030=30029    => 3607203599/30030=120119  = 30029+1)*i+60059
                            //  90091*60059 = 5410775369 => 5410775369%30030=30029    => 5410775369/30030=180178  (+60059)
                            // 120121*60059 = 7214347139 => 7214347139%30030=30029    => 7214347139/30030=240237  (+60059)
                        }
                    }
                }
            }

            mult30030 += NB_IN_BLOCK;
        }
    }

    /**
     *
     * @param cacheBitShift
     * @param ib line in [1..NB_BITS-2]
     * @param jb column, in [ib..NB_BITS-2]
     * @return bitShift in [0..NB_BITS-1]
     */
    private static short getCacheBitShift(short[] cacheBitShift, int ib, int jb)
    {
//        if (ib + jb < NB_BITS)
//        {
//            final int ib2 = ib - 1;
//            final int jb2 = jb - 1;
//            //(1 1) => 0;   (1 2) => 1;     (1 3) => 2;     (1 4) => 3;     (1 5) => 4;     (1 6) => 5;
//            //(2 2) => 6;   (2 3) => 7;     (2 4) => 8;     (2 5) => 9;
//            //(3 3) => 10;  (3 4) => 11; 
//            //ex: ib == 3, jb == 4 --> index 11 : res == 4 (0000_0100);
//            return cacheBitShift[ib2 * (NB_BITS - 2) + jb2 - ib2 * ib2];
//        }
//        else
//        {
        //ex: ib == 5, jb == 6 --> res == 16 (0001_0000)
        final int ib2 = NB_BITS - 2 - jb;   //0
        final int jb2 = NB_BITS - 2 - ib;   //1
        //ib_bis == 1, jb_bis == 2 --> index 1 : res == 16 (0001_0000)
        return cacheBitShift[ib2 * (NB_BITS - 2) + jb2 - ib2 * ib2];
//        }
    }

    public static long countPrimes(final long[] tab, final long nMax)
    {
        if (nMax < 17L)
        {
            if (nMax < 2)
            {
                return 0;
            }
            if (nMax < 3)
            {
                return 1;
            }
            if (nMax < 5)
            {
                return 2;
            }
            if (nMax < 7)
            {
                return 3;
            }
            if (nMax < 11)
            {
                return 4;
            }
            if (nMax < 13)
            {
                return 5;
            }
            return 6;
        }
        //long nbPrimes = 6;
        long nbNotPrimes = 0;

        final int nbBlocks = (int) (((nMax - 1) / NB_IN_BLOCK) + 1);   //for 100_000, we need 4 blocks of 30030,
        final int size = nbBlocks * NB_LONG;                        // with 90 longs each = 360 longs for 4 blocks

        for (int i = 0; i < (nbBlocks - 1) * NB_LONG; i++)
        {
            long bits = tab[i];
            //count of bit to "1" in a long : https://cs108.epfl.ch/archive/15/files/ppo15_09_entiers_4.pdf
            //nbPrimes += 64 - Long.bitCount(bits);
            nbNotPrimes += Long.bitCount(bits);
        }

        //System.out.println("nbBlocks : " + nbBlocks + " / nbNotPrimes : " + nbNotPrimes + " ==> res : " + ((((nbBlocks-1)*90)<<6) - nbNotPrimes + 6));
        long nbPrimes = (((nbBlocks-1)*90L)<<6) - nbNotPrimes + 6;

        //last long
        final int iLastBlock = nbBlocks - 1;
        final int iLast = iLastBlock * NB_LONG;
        int ib = 0;
        for (int iLong = 0; iLong < NB_LONG; iLong++)
        {
            int iTab = iLast + iLong;
            if (iTab >= size)
            {
                break;
            }
            long bits = tab[iTab];
            for (int bitShift = 0; bitShift < 64; bitShift++)
            {
                if ((bits & (BIT_63 >>> bitShift)) == 0b0)
                {
                    long v1 = 30030L * (nbBlocks - 1) + TAB_ELTS[ib];
                    if (v1 > nMax || v1 < 0)
                    {
                        break;
                    }
                    nbPrimes++;
                }
                ib++;
            }
        }

        return nbPrimes;
    }

    public static ArrayList<Long> getListPrimes(final long[] tab, final long nMax)
    {
        return getListPrimes(tab, nMax, -1);
    }

    public static ArrayList<Long> getListPrimes(final long[] tab, final long nMax, int nbPrimes)
    {
        if(nbPrimes == -1)
            nbPrimes = (int) (nMax / Math.log(nMax) * 1.1);
        final ArrayList<Long> listPrimes = new ArrayList<>(nbPrimes);

        if (nMax > 13L)
        {
            listPrimes.add(2L);
            listPrimes.add(3L);
            listPrimes.add(5L);
            listPrimes.add(7L);
            listPrimes.add(11L);
            listPrimes.add(13L);
        }
        else
        {
            if (nMax >= 2)
            {
                listPrimes.add(2L);
            }
            if (nMax >= 3)
            {
                listPrimes.add(3L);
            }
            if (nMax >= 5)
            {
                listPrimes.add(5L);
            }
            if (nMax >= 7)
            {
                listPrimes.add(7L);
            }
            if (nMax >= 11)
            {
                listPrimes.add(11L);
            }
            if (nMax >= 13)
            {
                listPrimes.add(13L);
            }
            return listPrimes;
        }

        final int nbBlocks = (int) (((nMax - 1) / NB_IN_BLOCK) + 1);   //for 100_000, we need 4 blocks of 30030,
        long mult30030 = 0;

        for (int iBlock = 0; iBlock < nbBlocks - 1; iBlock++)
        {
            final int i = iBlock * NB_LONG;
            int ib = 0;
            for (int iLong = 0; iLong < NB_LONG; iLong++)
            {
                final int iTab = i + iLong;
                long bits = tab[iTab];
                if(bits == 0xFFFF_FFFF_FFFF_FFFFL) {    //all bits to 1 -> 0 prime number on this small block.
                    ib+=64;
                }
                else {
                    for (int bitShift = 0; bitShift < 64; bitShift++) {
                        if ((bits & (BIT_63 >>> bitShift)) == 0b0) {
                            long v1 = mult30030 + TAB_ELTS[ib];
                            listPrimes.add(v1);
                        }
                        ib++;
                    }
                }
            }
            mult30030 += 30030;
        }

        final int iLast = (nbBlocks - 1) * NB_LONG;
        int ib = 0;
        outerLoop:
        for (int iLong = 0; iLong < NB_LONG; iLong++)
        {
            final int iTab = iLast + iLong;
            long bits = tab[iTab];
            for (int bitShift = 0; bitShift < 64; bitShift++)
            {
                if ((bits & (BIT_63 >>> bitShift)) == 0b0)
                {
                    long v1 = mult30030 + TAB_ELTS[ib];
                    if (v1 > nMax)
                    {
                        break outerLoop;
                    }
                    listPrimes.add(v1);
                }
                ib++;
            }
        }

        return listPrimes;
    }
}
