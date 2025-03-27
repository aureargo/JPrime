/*
 * Copyright (C) janvier 2024
 * Thales LAS France SAS - all rights reserved
 */
package jprime;

import java.util.ArrayList;

public class PrimesGeneratorTabEven8Bits
{
    private static final int NB_BITS = 8;

    //100_000       -> 2 ms             => 9592
    //1_000_000     -> 13 ms            => 78498
    //10_000_000    -> 45 ms            => 664579
    //100_000_000   -> 212 ms           => 5761455
    //500_000_000   -> 1924 ms          => 26355867
    //1_000_000_000 -> 4312 ms          => 50847534
    //2_000_000_000 -> 8987 ms          => 98222287
    //Integer.MAX_VALUE -> 10257 ms     => 105097565
    //10_000_000_000l   -> 53932 ms     => 455052511
    //32_000_000_000l   -> 200464 ms    => 1382799415

    /**
     *
     * @param nMax in bound [2..32_000_000_000]
     * @return
     */
    public static byte[] generateTab(final long nMax)
    {
//        0 = 3  X      -> 10010010 01001001 00100100
//        1 = 5   Y     -> 01000010 00010000 10000100 00100001 00001000
//        2 = 7    Z    -> 00100000 01000000 10000001 00000010 00000100 00001000 00010000
//        3 = 9  X
//        4 = 11
//        5 = 13
//        6 = 15 XY
//        7 = 17
//        8 = 19
//        9 = 21 X Z
//        10= 23
//        11= 25  Y
//        12= 27 X
//        13= 29
//        14= 31
//        15= 33 X
//        16= 35  YZ
//        17= 37
//        18= 39 X
//        19= 41
//        20= 43
//        21= 45 XY
//        22= 47
//        23= 49   Z
//        tab[i] = (i+1)*2 +1;
        //31= 65
        //63= 129

        final long nbBitsInTab = ((nMax - 1) / 2);
        int sizeTab = (int) (nbBitsInTab / NB_BITS);
        if ((nbBitsInTab & 0b111) != 0) // %8
        {
            sizeTab++;
        }

        final byte[] tab = new byte[sizeTab];   // logique of jprimeBoolTab, but with 8 true/false by byte. ex : [0,0,0,1,0,0,1,0] for multiple of 3 (9 and 15) in "one single byte"

        final int sqrtN = (int) Math.sqrt(nMax);
        final int sqrtIBit = (sqrtN - 1) / 2 - 1;

        {
            final int sizePattern = 3 * 5 * 7;  //=105: tous les 105 cases, les multiplieurs de 3, 5 et 7 forment le meme pattern sur les bytes.
            byte[] tabPattern = new byte[sizePattern];
            int iBit = 0;
            for (int x = 3; x < NB_BITS; x += 2)
            {
                if (x > nMax)
                {
                    break;
                }
                byte[] tabPattern2 = new byte[x];

                for (int jBit = iBit; jBit < x * NB_BITS; jBit += x)
                {
                    int jOctet = jBit / NB_BITS;
                    int jInOctet = jBit & 0b111;  //[0..7]
                    tabPattern2[jOctet] |= 1 << jInOctet; // => is not prime
                }

                for (int iOctet = 0; iOctet < sizePattern; iOctet++)
                {
                    int jOctet = iOctet % x;
                    byte octet = tabPattern2[jOctet];
                    tabPattern[iOctet] |= octet;
                }

                iBit++;
            }

            tab[0] = (byte) (tabPattern[0] & (0xFF - (0b111)));
            for (int i = 1; i < sizeTab; i++)
            {
                int iPattern = i % sizePattern;
                byte pattern = tabPattern[iPattern];
                tab[i] = pattern;
            }

            long iSqBit = 39;
            int magicIndexAdd = 20;
            for (; iBit <= sqrtIBit; iBit++)
            {
                int iOctet = iBit / NB_BITS;
                byte octet = tab[iOctet];
                int iInOctet = iBit & 0b111;  //[0..7]
                boolean isPrime = 0 == (octet & (1 << iInOctet));

                if (isPrime)
                {
                    int x = ((iBit + 1) << 1) | 0x1;   // <--> x =(i+1)*2 +1;   //if i==2, so x == 7
                    //                int xSq = x * x;  //sous x*x, les cases multiples de x on deja ete cochees par le parcours d'autre nombres premiers
                    
                    //autre propriete remarquable: 3, 5,7,9,11,13,15 ... au carre == 9,25,49,81,121,169,225,289 ...
                    // qui ont l'index  3, 11, 23, 39, 59, 83, 111, 143 ...
                    // incroyable: a chaque iteration, on a +8, +12, +16, +20, +24, +28, +32... On ajoute 4 a un nombre ajouter precedemment en commencant par 8
                    // for i = 2 so x = 7 => iSqBit = 23 so x*x = 49
                    for (long jBit = iSqBit; jBit < nbBitsInTab; jBit += x) //we say that all multiplier of this "x" are not prime numbers.
                    {
                        //we change (i=9, i=16,) i=23, i=30 ... i=2+k*7 --> correspond to (x=21, x=35,) x=49, x=63 ... x=7+k*14
                        int jOctet = (int) (jBit / NB_BITS);
//                        if (jOctet < 0)
//                        {
//                            System.out.println(x + " / nbPrimes = " + nbPrimes + " / iBit = " + iBit + " / jBit = " + jBit
//                                    + " / nbBitInTab = " + nbBitsInTab
//                                    + " / iSqBit = " + iSqBit + " / sqrtIBit " + sqrtIBit + " / jOctet = " + jOctet);
//                        }
                        int jInOctet = (int) (jBit & 0b111);
                        tab[jOctet] |= 1 << jInOctet; // => is not prime
                    }
                }
                iSqBit += magicIndexAdd;
                magicIndexAdd += 4;
            }
        }

        return tab;
    }
    
    public static int countPrimes(final byte[] tab, final long nMax)
    {
        if(nMax < 2)
            return 0;
        
        int nbPrimes = 1;
        final int size = tab.length;
        for(int i = 0; i < size-1; i++)
        {
            byte bits = tab[i];
            //count of bit to "1" in a byte : https://cs108.epfl.ch/archive/15/files/ppo15_09_entiers_4.pdf
            bits = (byte) ((bits & 0b0101_0101) + ((bits & 0b1010_1010) >>> 1));  //-121 = 1000_0111 --> 0000_0101 + 0100_0001 = 0100_0110 (1 bit on 0xB0, 0 on 0x30, 1 on 0x0B and 2 on 0x03)
            bits = (byte) ((bits & 0b0011_0011) + ((bits & 0b1100_1100) >>> 2));  //                 --> 0000_0010 + 0001_0001 = 0001_0011 (1 bit on 0xF0 and 3 in 0x0F)
            bits = (byte) ((bits & 0b0000_1111) + ((bits & 0b1111_0000) >>> 4));  //                 --> 0000_0011 + 0000_0001 = 0000_0100 = 4 bit to "1"
            nbPrimes += 8 - bits;
        }
        
        byte bits = tab[size-1];
        final long nbBitsInTab = ((nMax - 1) / 2);
        for (int iBit = 0; iBit < (nbBitsInTab - (size - 1) * 8L); iBit++)
        {
            boolean isPrime = 0 == (bits & (1 << iBit));
            if(isPrime)
                nbPrimes++;
        }
        
        return nbPrimes;
    }
    
    public static ArrayList<Long> getListPrimes(final byte[] tab, final long nMax)
    {
        ArrayList<Long> listPrimes = new ArrayList<>((int)(nMax/Math.log(nMax)*1.1));
        if(nMax < 2)
            return listPrimes;
        
        listPrimes.add(2L);
        final int size = tab.length;
        for(int i = 0; i < size-1; i++)
        {
            byte bits = tab[i];
            long offset = i << 4L; //i*16;
            for(int iBit = 0;   iBit < NB_BITS;    iBit++)
            {
                boolean isPrime = 0 == (bits & (1 << iBit));
                if (isPrime)
                {
                    long x = offset + (((iBit + 1) << 1) | 0x1);
                    listPrimes.add(x);
                }
            }
        }
        
        byte bits = tab[size-1];
        final long nbBitsInTab = ((nMax - 1) / 2);
        long offset = (size - 1) << 4L; //i*16;
        for (int iBit = 0; iBit < (nbBitsInTab - (size - 1) * 8L); iBit++)
        {
            boolean isPrime = 0 == (bits & (1 << iBit));
            if (isPrime)
            {
                long x = offset + (((iBit + 1) << 1) | 0x1);
                listPrimes.add(x);
            }
        }

        return listPrimes;
    }
}
