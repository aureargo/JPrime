/*
 * Copyright (C) janvier 2024
 * Thales LAS France SAS - all rights reserved
 */
package jprime;

import java.util.ArrayList;

public class PrimesGeneratorTabEven64Bits
{
    private static final int NB_BITS = 64;

    //1_000_000         -> 10 ms            => 78498
    //10_000_000        -> 33 ms            => 664579
    //100_000_000       -> 187 ms           => 5761455
    //500_000_000       -> 2286 ms          => 26355867
    //1_000_000_000     -> 5038 ms          => 50847534
    //Integer.MAX_VALUE -> 11837 ms     => 105097565
    //10_000_000_000l   ->  ms     => 455052511
    //32_000_000_000l   ->  ms    => 1382799415

    /**
     *
     * @param n in bound [2..128_000_000_000]
     */
    public static long[] generateTab(final long n)
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
//        24= 51 X
//        25= 53
//        26= 55  Y
//        27= 57 X
//        28= 59
//        29= 61
//        30= 63 X Z
//        31= 65  Y
//        tab[i] = (i+1)*2 +1;
        //63= 129

        long nbBitsInTab = ((n - 1) / 2);
        final int sizeTab = (int) (nbBitsInTab / NB_BITS) + ((nbBitsInTab & 0x3F) != 0 ? 1 : 0);

        long[] tab = new long[sizeTab];   // logique of jprimeBoolTab, but with 64 true/false by long
        ///////////////////////////////////////////////////
        long firstLong = computeFirstLong();
        tab[0] = firstLong;
        ///////////////////////////////////////////////////

        int sqrtN = (int) Math.sqrt(n);
        int sqrtIBit = (sqrtN - 1) / 2 - 1;

        int nbPrime = 1;    // admit 2 is prime;
//        ExecutorService executorService = Executors.newSingleThreadExecutor();

        {
            int iBit = 0;   //[0..30]
            {
                int x = 3;      //[3..63]

                do
                {
                    boolean isPrime = (firstLong & (1l << iBit)) == 0;
                    if (isPrime)
                    {
                        nbPrime++;
                        final int x2 = x;
                        final int iBit2 = iBit;
                        final byte[] tabPattern = new byte[x2];
                        for (int jBit = iBit2; jBit < x2 * 8; jBit += x2)
                        {
                            int j1Octet = jBit / 8;
                            int jIn1Octet = jBit & 0b111;  //[0..7]
                            tabPattern[j1Octet] |= 1 << jIn1Octet; // => is not prime
                        }

//                        executorService.submit(new Runnable()
//                        {
//                            @Override
//                            public void run()
//                            {

                                for (int i = 0; i < x2; i++)
                                {
                                    long valLong = ((tabPattern[(i * 8 + 7) % x2] & 0xFFl) << 56)
                                            | ((tabPattern[(i * 8 + 6) % x2] & 0xFFl) << 48)
                                            | ((tabPattern[(i * 8 + 5) % x2] & 0xFFl) << 40)
                                            | ((tabPattern[(i * 8 + 4) % x2] & 0xFFl) << 32)
                                            | ((tabPattern[(i * 8 + 3) % x2] & 0xFFl) << 24)
                                            | ((tabPattern[(i * 8 + 2) % x2] & 0xFFl) << 16)
                                            | ((tabPattern[(i * 8 + 1) % x2] & 0xFFl) << 8)
                                            | ((tabPattern[(i * 8 + 0) % x2] & 0xFFl) << 0);
                                    //System.out.println(x + " " + i + " " + Long.toBinaryString(valLong));
                                    for (int iLong = (i == 0 ? x2 : i); iLong < sizeTab; iLong += x2)
                                    {
                                        tab[iLong] |= valLong;
                                    }
                                }

//                            }
//                        });
                    }

                    iBit++;
                    x += 2;
                }
                while (x < NB_BITS);
            }

//            System.out.println("tab[0] = " + Long.toBinaryString(tab[0]));
//            System.out.println("tab[1] = " + Long.toBinaryString(tab[1]));
//            System.out.println("tab[2] = " + Long.toBinaryString(tab[2]));
//            System.out.println("tab[3] = " + Long.toBinaryString(tab[3]));
//            System.out.println("tab[4] = " + Long.toBinaryString(tab[4]));
//            System.out.println("tab[5] = " + Long.toBinaryString(tab[5]));
//            executorService.shutdown();

            ///////////////////////////////////////////////////
            ///////////////////////////////////////////////////
            for (; iBit <= sqrtIBit; iBit++)    //iBit = 31 at init
            {
                int i8Octets = iBit / NB_BITS;
                long valLong = tab[i8Octets];
                int iIn8Octets = iBit & 0x3F;  //[0..63]
                boolean isPrime = 0 == (valLong & (1l << iIn8Octets));

                if (isPrime)
                {
                    long x = ((iBit + 1) << 1) | 0x1;   // <--> x =(i+1)*2 +1;   //if i==2, so x == 7
                    for (long jBit = iBit + x; jBit < nbBitsInTab; jBit += x)    //we say that all multiplier of this "x" are not prime numbers.
                    {
                        //we change i=9, i=16, i=23, i=30 ... i=2+k*7 --> correspond to x=21, x=35, x=49, x=63 ... x=7+k*14
                        int j8Octets = (int) (jBit / NB_BITS);
                        int jIn8Octets = (int) (jBit) & 0x3F;
                        tab[j8Octets] |= 1l << jIn8Octets; // => is not prime
                    }
                }
            }
        }

//        long[] tabPrimes = new long[nbPrime];
//        tabPrimes[0] = 2l;
//        int iPrime = 1;
//        for (int i8Octets = 0, iBit = 0; i8Octets < sizeTab; i8Octets++)
//        {
//            long valLong = tab[i8Octets];
//            for (int iIn8Octets = 0; iIn8Octets < NB_BITS && iBit < nbBitsInTab; iIn8Octets++, iBit++)
//            {
//                boolean isPrime = 0 == (valLong & (1l << iIn8Octets));
//                if (isPrime)
//                {
//                    long x = ((iBit + 1l) << 1) | 0x1;   //if i==2, so x == 7
//                    tabPrimes[iPrime] = x;
//                    iPrime++;
//                }
//            }
//        }

//        for (int iBit = 0; iBit < nbBitsInTab; iBit++)
//        {
//            int i8Octets = iBit / NB_BITS;
//            long valLong = tab[i8Octets];
//            int iIn8Octets = iBit & 0x3F;  //[0..63]
//            boolean isPrime = 0 == (valLong & (1l << iIn8Octets));
//
//            if (isPrime)
//            {
//                long x = ((iBit + 1l) << 1) | 0x1;   //if i==2, so x == 7
//                tabPrimes[iPrime] = x;
//                iPrime++;
//            }
//        }

//        System.out.println("nb of primary numbers = " + nbPrime);
//
//        StringBuilder stringBuilder = new StringBuilder(nbPrime * 4);
//        int i = 0;
//        long val = tabPrimes[i];
//        stringBuilder.append(val);
//        for (i = 1; i < nbPrime; i++)
//        {
//            val = tabPrimes[i];
//            stringBuilder.append(';').append(val);
//        }
//        System.out.println("[" + stringBuilder.toString() + ']');
//        stringBuilder.delete(0, Integer.MAX_VALUE); //clear() (if only it's implement in StringBuilder class)
        return tab;
    }

    private static long computeFirstLong()
    {
        long resFirstLong = 0;

        int iSqBit = 3;
        int magicIndexAdd = 8;
        for (int iBit = 0; iBit < Math.sqrt(NB_BITS); iBit++)
        {
            boolean isPrime = (resFirstLong & (1l << iBit)) == 0;
            if (isPrime)
            {
                int x = ((iBit + 1) << 1) | 0x1;   // <--> x =(i+1)*2 +1;   //if i==2, so x == 7
//                int xSq = x * x;  //sous x*x, les cases multiples de x on deja ete cochees par le parcours d'autre nombres premiers

                //autre propriete remarquable: 3, 5,7,9,11,13,15 ... au carre == 9,25,49,81,121,169,225,289 ...
                // qui ont l'index  3, 11, 23, 39, 59, 83, 111, 143 ...
                // incroyable: a chaque iteration, on a +8, +12, +16, +20, +24, +28, +32... On ajoute 4 a un nombre ajouter precedemment en commencant par 8
                // for i = 2 so x = 7 => iSqBit = 23 so x*x = 49
                for (int j = iSqBit; j < NB_BITS; j += x)    //we say that all multiplier of this "x" are not prime numbers.
                {
                    //for x = 7 we change (i=9, i=16,) i=23, i=30 ... i=2+k*7 --> correspond to (x=21, x=35,) x=49, x=63 ... x=7+k*14
                    resFirstLong |= 1l << j; // => is not prime
                }
            }
            iSqBit += magicIndexAdd;
            magicIndexAdd += 4;
        }

        return resFirstLong;
    }

    public static long countPrimes(final long[] tab, final long nMax)
    {
        if (nMax < 2)
        {
            return 0;
        }

        long nbNotPrimes = 0;
        final int size = tab.length;
        for (int i = 0; i < size - 1; i++)
        {
            long bits = tab[i];
            nbNotPrimes += Long.bitCount(bits);
        }
        long nbPrimes = (size - 1) * 64L - nbNotPrimes + 1;

        final long bits = tab[size - 1];
        final long nbBitsInTab = ((nMax - 1) / 2);
        for (int iBit = 0; iBit < (nbBitsInTab - (size - 1) * 64L); iBit++)
        {
            boolean isPrime = 0L == (bits & 1L << iBit);
            if (isPrime)
            {
                nbPrimes++;
            }
        }

        return nbPrimes;
    }

    public static ArrayList<Long> getListPrimes(final long[] tab, final long nMax)
    {
        ArrayList<Long> listPrimes = new ArrayList<>((int) (nMax / Math.log(nMax) * 1.1));
        if (nMax < 2)
        {
            return listPrimes;
        }

        listPrimes.add(2L);
        final int size = tab.length;
        for (int i = 0; i < size - 1; i++)
        {
            long bits = tab[i];
            long offset = i << 7L; //i*128;
            for (int iBit = 0; iBit < NB_BITS; iBit++)
            {
                boolean isPrime = 0L == (bits & (1L << iBit));
                if (isPrime)
                {
                    long x = offset + (((iBit + 1) << 1) | 0x1);
                    listPrimes.add(x);
                }
            }
        }

        final long bits = tab[size - 1];
        final long nbBitsInTab = ((nMax - 1) / 2);
        final long offset = (size - 1) << 7L; //i*128;
        for (int iBit = 0; iBit < (nbBitsInTab - (size - 1) * 64L); iBit++)
        {
            boolean isPrime = 0 == (bits & (1L << iBit));
            if (isPrime)
            {
                long x = offset + (((iBit + 1) << 1) | 0x1);
                listPrimes.add(x);
            }
        }

        return listPrimes;
    }
}
