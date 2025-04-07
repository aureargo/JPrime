/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jprime;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 30n+1 //(1), 31, 61, (91),(121)
 * 30n+7 // 7, 37, 67, 97, 127
 * 30n+11 // 11, 41, 71, 101, 131
 * 30n+13 // 13, 43, 73, 103, 133
 * 30n+17 // 17, 47,(77),107, 137
 * 30n+19 // 19,(49),79, 109, 139
 * 30n+23 // 23, 53, 83, 113,(143)
 * 30n+29 // 29, 59, 89, 119, 149
 * <p>
 * The objectif in this class is to minimise "cache tabs" of mask and mult/30 in preparation of new algo 2*3*5 =30 on 1 byte => 2*3*5*7*11*13 = 30300 on 480 bytes (60 longs)
 */
public class PrimesGeneratorTabMod30 {

    private static final int NB_IN_BLOCK = 30;
    private static final int NB_BITS = 8;
    private static final byte[] TAB_ELTS =
            {
                    1, 7, 11, 13, 17, 19, 23, 29
            };
    private static final byte LAST_ELT = TAB_ELTS[NB_BITS - 1];
    private static final byte BIT_7 = (byte) 0b1000_0000;

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
    //64_000_000_000L    290074 ms   //2_685_000_601

    /**
     * @param nMax [0..64_000_000_000L]
     * @return nb nombre premier <= n
     */
    public static byte[] generateTab(long nMax) {
        long start = System.currentTimeMillis();
        long end;

        final int size = (int) (((nMax - 1) / NB_IN_BLOCK) + 1);

        byte[] tab = new byte[size];

        end = System.currentTimeMillis();
        System.out.println("allocation execution time = " + (end - start) + " ms");
        start = end;

        tab[0] = BIT_7;   //only 1 is not prime
        //tab for cache
        int sqrtN = (int) Math.sqrt(nMax);

        //tab for cache
        final byte[] cacheMask = new byte[6 + 4 + 2];
        final byte[] cacheMultDiv30 = new byte[(6 * 7) / 2];
        byte lastMod = 1;
        byte lastShift = 0;
        for (int ib = 1, iMask = 0, iMultDiv30 = 0; ib < NB_BITS - 1; ib++) {
            final int v1 = TAB_ELTS[ib];
            for (int jb = ib; jb < NB_BITS - 1; jb++) {
                final int v2 = TAB_ELTS[jb];
                final int v = v1 * v2;
                final byte iTab = (byte) (v / NB_IN_BLOCK);    //23*23 = 529 => 529/30 = index 17 => 529%30 = 19
                cacheMultDiv30[iMultDiv30] = iTab;
                iMultDiv30++;

                if (ib + jb < NB_BITS) {
                    // => 529%30 = 19 => bit 6 (5)
                    final byte modulo = (byte) (v - NB_IN_BLOCK * iTab);
                    if (modulo > lastMod) {
                        lastShift = (byte) Arrays.binarySearch(TAB_ELTS, lastShift + 1, NB_BITS, modulo);
                    } else {
                        lastShift = (byte) Arrays.binarySearch(TAB_ELTS, 0, lastShift, modulo);
                    }
                    lastMod = modulo;
                    cacheMask[iMask] = (byte) ((BIT_7 & 0xFF) >>> lastShift); // => 0010_0000
                    iMask++;
                }
            }
        }
        //[   1,    2,    4,    8,   16,   32,   64, -128]  =>  == 0b1 << jb
        //[   2,   32,   16,    1, -128,    8,    4,   64]  =>        [   32,   16,    1, -128,    8,    4]
        //[   4,   16,    1,   64,    2, -128,    8,   32]  =>              [    1,   64,    2, -128]
        //[   8,    1,   64,   32,    4,    2, -128,   16]  =>                    [   32,    4]
        //[  16, -128,    2,    4,   32,   64,    1,    8]  =>  
        //[  32,    8, -128,    2,   64,    1,   16,    4]  =>  
        //[  64,    4,    8, -128,    1,   16,   32,    2]  =>  
        //[-128,   64,   32,   16,    8,    4,    2,    1]  =>  

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
//
        end = System.currentTimeMillis();
        System.out.println("init execution time = " + (end - start) + " ms");
        start = end;

        int iMask = 0;
        int iMultDiv30 = 0;
        for (int ib = 1; ib < NB_BITS; ib++) {
            final int v1 = TAB_ELTS[ib];
            if (v1 > sqrtN) {
                break;
            }
            final byte bitsAnd1 = (byte) ((BIT_7 & 0xFF) >>> ib);
            for (int iTab = v1; iTab < size; iTab += v1)    //els[ib]*(30k+1) with k>=1 --> 7*31, 7*61, 7*91, 7*121
            {
                tab[iTab] |= bitsAnd1;
                //(7*1  =   7 =>   7%30=7    =>   7/30=0)
                // 7*31 = 217 => 217%30=7    => 217/30=7
                // 7*61 = 427 => 427%30=7    => 427/30=14
                //=> 7*(30k+1) %30 don't change with +30 in multiplier, and +7 in tab index for each +30 
            }

            for (int jb = ib; jb < NB_BITS - 1; jb++)    //els[ib]*(30k+els[jb]) with k>=0 and x>=1 --> 7*7, 7*37, 7*67...7*11, 7*41, 7*71...7*29, 7*59...
            {
                final byte bitsAnd7to23 = (ib + jb < NB_BITS ? cacheMask[iMask++] : getCacheMask(cacheMask, ib, jb));
                final int iTabInit = cacheMultDiv30[iMultDiv30++];    //getCacheMultDiv30(cacheMultDiv30, ib, jb);
                for (int iTab = iTabInit; iTab < size; iTab += v1) {
                    tab[iTab] |= bitsAnd7to23;
                    //7*11 =  77 =>  77%30=17    =>  77/30=2
                    //7*41 = 287 => 287%30=17    => 287/30=9
                    //7*71 = 497 => 497%30=17    => 497/30=16
                    //=> 7*(30k+11) %30 don't change with +30 in multiplier, and +7 in tab index for each +30 starting by (7*11)/30 = 2
                }
                if (ib != jb) {
                    final int v2 = TAB_ELTS[jb];
                    for (int iTab = iTabInit + v2; iTab < size; iTab += v2) {
                        tab[iTab] |= bitsAnd7to23;
                        //(7*11 =  77 =>  77%30=17    =>  77/30=2)
                        // 37*11 = 407 => 407%30=17    => 407/30=13
                        // 67*11 = 737 => 737%30=17    => 737/30=24
                        //=> 7*(30k+11) %30 don't change with +30 in multiplier, and +11 in tab index for each +30 starting by (37*11)/30 = 2+11
                    }
                }
            }

            final byte bitsAnd29 = (byte) ((BIT_7 & 0xFF) >>> (NB_BITS - 1 - ib));
            final int iTabInit = v1 - 1;
            for (int iTab = iTabInit; iTab < size; iTab += v1) {
                tab[iTab] |= bitsAnd29;
            }
            if (ib != NB_BITS - 1) {
                final int v2 = LAST_ELT;  // == 29
                for (int iTab = iTabInit + v2; iTab < size; iTab += v2) {
                    tab[iTab] |= bitsAnd29;
                }
            }
        }

        ////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////
        end = System.currentTimeMillis();
        System.out.println("mini loop elimination non-primes execution time = " + (end - start) + " ms");
        start = end;
        ////////////////////////////////////////////////////////////////////////
        //////////////////////////////Second loop///////////////////////////////

        int mult30 = NB_IN_BLOCK;
        outerloop:
        for (int i = 1; i < size; i++) {
            if (mult30 + 1 > sqrtN) {
                break outerloop;
            }

            final byte bits = tab[i];

            ////////////////////////////////////////////////////////////////////
            //31 61 91 121
            final boolean isV1Prime31 = (bits & BIT_7) == 0b0;
            for (int jb = 0; jb < NB_BITS; jb++) {
                final int v2 = mult30 + TAB_ELTS[jb];
                final int iTabInit = (v2 + 1) * i;
                final byte bitsAnd31With1to29 = (byte) ((BIT_7 & 0xFF) >>> jb);
                if (isV1Prime31) {
                    for (int iTab = iTabInit; iTab < size; iTab += mult30 + 1) {
                        tab[iTab] |= bitsAnd31With1to29;
                        // 31*37 = 1147 => 1147%30=7    => 1147/30= 38  = 37+1
                        // 31*67 = 2077 => 2077%30=7    => 2077/30= 69  (+31)
                        // 31*97 = 3007 => 3007%30=7    => 3007/30=100  (+31)

                        // 61*67  = 4087 => 4087%30=7    => 4087/30= 136  = (67+1)*2
                        // 61*97  = 5917 => 5917%30=7    => 5917/30= 197  (+61)
                        // 61*127 = 7747 => 7747%30=7    => 7747/30= 258  (+61)
                        // 91*97  = 8827 => 8827%30=7    => 8827/30= 294  = (97+1)*3
                    }
                }
                final boolean isV2Prime = (bits & bitsAnd31With1to29) == 0b0;
                if (jb != 0 && isV2Prime) {
                    for (int iTab = iTabInit + v2; iTab < size; iTab += v2) {
                        tab[iTab] |= bitsAnd31With1to29;
                        // 61*37 = 2257 => 2257%30=7    => 2257/30= 75   = 37+1 +31
                        // 91*37 = 3367 => 3367%30=7    => 3367/30=112   (+37)

                        // 91*67 = 6097 => 6097%30=7    => 6097/30=203 = = (67+1)*2 + 67
                        // 121*67= 8107 => 8107%30=7    => 8107/30=270   (+67)
                    }
                }
            }

            iMask = 0;
            iMultDiv30 = 0;
            for (int ib = 1; ib < NB_BITS; ib++) {
                final int v1 = mult30 + TAB_ELTS[ib];
                if (v1 > sqrtN) {
                    break outerloop;
                }

                final byte bitsAndV1 = (byte) ((BIT_7 & 0xFF) >>> ib);
                final boolean isV1Prime = (bits & bitsAndV1) == 0b0;

                for (int jb = ib; jb < NB_BITS - 1; jb++)    //els[ib]*(30k+els[jb]) with k>=0 and x>=1 --> 7*7, 7*37, 7*67...7*11, 7*41, 7*71...7*29, 7*59...
                {
                    final int iTabInit = (v1 + TAB_ELTS[jb]) * i + cacheMultDiv30[iMultDiv30++];    //getCacheMultDiv30(cacheMultDiv30, ib, jb);

                    final byte bitsAndV2 = (byte) ((BIT_7 & 0xFF) >>> jb);
                    final boolean isV2Prime = (bits & bitsAndV2) == 0b0;

                    final byte bitsAnd7to23 = (ib + jb < NB_BITS ? cacheMask[iMask++] : getCacheMask(cacheMask, ib, jb));
                    //mod(30): 37*43 = (30+7)*(30+13) => (a+b)*(a+c) = a*a + ab + ac + bc => 900 + 30*7 + 30*13 + 7*13 => (mod 30) 0+0+0+ 91%30 = 1
                    //iTab  : (37*43)/30 = (30*30 + 30*7 + 30*13 + 91)/30 = 30+7+13+(91/30) = 53
                    //iTab  : (67*71)/30 = (60*60 + 60*7 + 60*11 + 77)/30 = 2*60 + 2*7 + 2*11 + (77/30) = (60 + 7 + 11)*2 + (77/30) = 158
                    if (isV1Prime) {
                        for (int iTab = iTabInit; iTab < size; iTab += v1) {
                            tab[iTab] |= bitsAnd7to23;
                            //37*43 = 1591 => 1591%30=1    => 1591/30= 53  = 30+7+13+((7*13)/30)
                            //37*73 = 2701 => 2701%30=1    => 2701/30= 90  (+37)
                            //37*103= 3811 => 3811%30=1    => 3811/30=127  (+37)
                            //=> 37*(30k+13) %30 don't change with +30 in multiplier, and +37 in tab index for each +30 starting by (37*43)%30 = 53
                        }
                    }
                    if (ib != jb && isV2Prime) {
                        final int v2 = mult30 + TAB_ELTS[jb];
                        for (int iTab = iTabInit + v2; iTab < size; iTab += v2) {
                            tab[iTab] |= bitsAnd7to23;
                            //(37*43 = 1591 => 1591%30=1    => 1591/30= 53)
                            // 67*43 = 2881 => 2881%30=1    => 2881/30= 96
                            // 97*43 = 4171 => 4171%30=1    => 4171/30=139
                            //=> (30k+7)*43 %30 don't change with +30 in multiplier, and +43 in tab index for each +30 starting by (37*43)%30 = 53
                        }
                    }
                    //PS: 49*53 = 2597 = 7*371 which as treat by 7*(11+30k)
                }

                final byte bitsAnd29 = (byte) ((BIT_7 & 0xFF) >>> (NB_BITS - 1 - ib));
                final int iTabInit = (v1 + LAST_ELT) * i + (TAB_ELTS[ib] - 1); //53*59 = 3127 => 3127%30=7 => 3127/30=104 == (53+29)*1 + 22    41*59 = 2419 => 2419%30=19 => 2419/80 = (41*29)*1 + 10
                if (isV1Prime) {
                    for (int iTab = iTabInit; iTab < size; iTab += v1) {
                        tab[iTab] |= bitsAnd29;
                        // 31*59 = 1829 => 1829%30=29    => 1829/30= 60  = 59+1
                        // 31*89 = 2759 => 2759%30=29    => 2759/30= 91  (+31)
                        // 31*119= 3689 => 3689%30=29    => 3689/30=122  (+31)
                    }
                }
                final boolean isV2Prime = (bits & ((BIT_7 & 0xFF) >>> (NB_BITS - 1))) == 0b0;
                if (ib != NB_BITS - 1 && isV2Prime) {
                    final int v2 = mult30 + LAST_ELT;  // == 59, 89, 119 ...
                    for (int iTab = iTabInit + v2; iTab < size; iTab += v2) {
                        tab[iTab] |= bitsAnd29;
                        //(31*59 = 1829 => 1829%30=29    => 1829/30= 60  = 59+1)
                        // 61*59 = 3599 => 3599%30=29    => 3599/30=119  (+59)
                        // 91*59 = 5369 => 5369%30=29    => 5369/30=178  (+59)
                    }
                }
            }

            mult30 += NB_IN_BLOCK;
        }

        end = System.currentTimeMillis();
        System.out.println("mega loop elimination non-primes execution time = " + (end - start) + " ms");

        return tab;
    }

    /**
     * @param cacheMask
     * @param ib        line in [1..6]
     * @param jb        column, in [ib..6]
     * @return mask in [0000_0001..1000_0000]
     */
    private static byte getCacheMask(byte[] cacheMask, int ib, int jb) {
        if (ib + jb < NB_BITS) {
            final int ib2 = ib - 1;
            final int jb2 = jb - 1;
            //(1 1) => 0;   (1 2) => 1;     (1 3) => 2;     (1 4) => 3;     (1 5) => 4;     (1 6) => 5;
            //(2 2) => 6;   (2 3) => 7;     (2 4) => 8;     (2 5) => 9;
            //(3 3) => 10;  (3 4) => 11; 
            //ex: ib == 3, jb == 4 --> index 11 : res == 4 (0000_0100);
            return cacheMask[ib2 * 6 + jb2 - ib2 * ib2];
        } else {
            //ex: ib == 5, jb == 6 --> res == 16 (0001_0000)
            final int ib2 = NB_BITS - 2 - jb;   //0
            final int jb2 = NB_BITS - 2 - ib;   //1
            //ib_bis == 1, jb_bis == 2 --> index 1 : res == 16 (0001_0000)
            return cacheMask[ib2 * 6 + jb2 - ib2 * ib2];
        }

    }

    //    /**
//     *
//     * @param cacheMultDiv30
//     * @param ib line in [1..6]
//     * @param jb column, in [ib..6]
//     * @return iTab in [1..17] --> (els[ib]*els[jb])/30
//     */
//    private static byte getCacheMultDiv30(byte[] cacheMultDiv30, int ib, int jb)
//    {
//        //ex: ib == 2, jb == 6 --> index 10 : res == 8
//        //ex: ib == 3, jb == 6 --> index 14 : res == 9
//        //ex: ib == 6, jb == 6 --> index 20 : res == 17
//        final int ib2 = ib - 1;     //1     2       5
//        final int jb2 = jb - 1;     //5     5      5
//        return cacheMultDiv30[ib2 * 6 + jb2 - ((ib2 * ib) >>> 1)];
//        //1*6+5-(1*2)/2 = 10      2*6+5-(2*3/2) = 14        5*6+5-(5*6/2)=20
//    }
    private static int inferiorTo30(long n) {
        if (n < 2) {
            return 0;
        } else if (n == 2) {
            return 1;
        } else if (n < 5) {
            return 2;
        } else if (n < 7) {
            return 3;
        } else if (n < 11) {
            return 4;
        } else if (n < 13) {
            return 5;
        } else if (n < 17) {
            return 6;
        } else if (n < 19) {
            return 7;
        } else if (n < 23) {
            return NB_BITS;
        } else if (n < 29) {
            return 9;
        } else {
            return 10;
        }
    }

    //    private static final int[] tabIndexMod
//            =
//            {
//                -2, 0, -1, -1, -1, -1, -1, 1, -1, -1,
//                -1, 2, -1, 3, -1, -1, -1, 4, -1, 5,
//                -1, -1, -1, 6, -1, -1, -1, -1, -1, 7
//            };
//
//    private static int indexMod(int index)
//    {
//        return tabIndexMod[index];
//    }
    public static long countPrimes(final byte[] tab, final long nMax) {
        if (nMax < NB_IN_BLOCK) {
            return inferiorTo30(nMax);
        }

        int nbPrimes = 3;
        final int size = tab.length;
        for (int i = 0; i < size - 1; i++) {
            byte bits = tab[i];
            bits = (byte) (0xFF - bits);
            //count of bit to "1" in a byte : https://cs108.epfl.ch/archive/15/files/ppo15_09_entiers_4.pdf
            bits = (byte) (bits - ((bits >>> 1) & 0b0101_0101));  //-121 = 1000_0111 --> 0000_0101 + 0100_0001 = 0100_0110 (1 bit on 0xB0, 0 on 0x30, 1 on 0x0B and 2 on 0x03)
            bits = (byte) ((bits & 0b0011_0011) + ((bits & 0b1100_1100) >>> 2));  //                 --> 0000_0010 + 0001_0001 = 0001_0011 (1 bit on 0xF0 and 3 in 0x0F)
            bits = (byte) ((bits + (bits >>> 4)) & 0b0000_1111);  //                 --> 0000_0011 + 0000_0001 = 0000_0100 = 4 bit to "1"
            nbPrimes += bits;
//            for (int ib = 0; ib < NB_BITS; ib++)
//            {
////                if ((bits & ((BIT_7 & 0xFF) >>> ib)) == 0b0)
////                {
////                    nbPrimes++;
////                }
//            }
        }

        //last byte
        final byte bits = tab[size - 1];
        for (int bitShift = 0; bitShift < NB_BITS; bitShift++) {
            if ((bits & ((BIT_7 & 0xFF) >>> bitShift)) == 0b0) {
                long v1 = (size - 1) * 30L + TAB_ELTS[bitShift];
                if (v1 > nMax) {
                    break;
                }
                nbPrimes++;
            }
        }

        return nbPrimes & 0xFFFF_FFFFL;
        //without cast to long, can be negative (if > Integer.MAX_VALUE)
    }

    public static ArrayList<Long> getListPrimes(final byte[] tab, final long nMax) {
        final ArrayList<Long> listPrimes = new ArrayList<>((int) (nMax / Math.log(nMax) * 1.1));

        if (nMax > 5L) {
            listPrimes.add(2L);
            listPrimes.add(3L);
            listPrimes.add(5L);
        } else {
            if (nMax >= 2) {
                listPrimes.add(2L);
            }
            if (nMax >= 3) {
                listPrimes.add(3L);
            }
            if (nMax >= 5) {
                listPrimes.add(5L);
            }
            return listPrimes;
        }

        final int size = tab.length;

        for (int iTab = 0; iTab < size - 1; iTab++) {
            byte bits = tab[iTab];
            for (int bitShift = 0; bitShift < NB_BITS; bitShift++) {
                if ((bits & ((BIT_7 & 0xFF) >>> bitShift)) == 0b0) {
                    long v1 = iTab * 30L + TAB_ELTS[bitShift];
                    listPrimes.add(v1);
                }
            }
        }

        final byte bits = tab[size - 1];
        for (int bitShift = 0; bitShift < NB_BITS; bitShift++) {
            if ((bits & ((BIT_7 & 0xFF) >>> bitShift)) == 0b0) {
                long v1 = (size - 1) * 30L + TAB_ELTS[bitShift];
                if (v1 > nMax) {
                    break;
                }
                listPrimes.add(v1);
            }
        }

        return listPrimes;
    }
}
