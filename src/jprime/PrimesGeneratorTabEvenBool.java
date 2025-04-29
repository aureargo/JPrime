package jprime;

import java.util.ArrayList;

public class PrimesGeneratorTabEvenBool
{
        //1_000_000     -> 13 ms
        //10_000_000    -> 75 ms
        //100_000_000   -> 700 ms
        //200_000_000   -> 1379 ms
        //500_000_000   -> 3490 ms
        //1_000_000_000 -> 6272 ms          => 50847534
        //Integer.MAX_VALUE -> 15165 ms     => 105097565

    /**
     * Dans un tableau de tous les nombres, parcourez les nombres un par un<br>
     * et cocher tous les multiples d'un nombres sauf lui même.
     * 2 => 123X5X7X9X0X0X0X0X0X0X0X
     * 3 => 12345X78X00X00X00X00X00X
     * 5 => 123456789X0000X0000X0000
     * 7 => 1234567890000X000000X000
     * 11=> 123456789000000000000X00
     * On a parfois coche plusieurs fois le meme nombre, mais si on superpose les resultats, on obtient:
     * 2|3|5|7|11
     * => 123X5X7XXX0X0XXX0X0XXX0X
     * => donc 2,3,5,7,11,13,17,19,23 ... ne sont pas coches et, incroyable, ils sont tous premiers.
     *
     * On enlève les cases correspondant aux nombres paires et a 1, et on obtient d'autres proprietes remarquable lors du parcours.
     *
     * @param n in bound [0..2**31-1]
     */
    public static boolean[] generateTab(final int n)
    {
//        0 = 3  X
//        1 = 5   Y
//        2 = 7    Z
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

        int sizeTab = (n - 1) / 2;  //if n == 101 ou 102, sizeTab == 50
        boolean[] tab = new boolean[sizeTab];   // all index at false are prime

        int sqrtN = (int) Math.sqrt(n);
        int sqrtIBit = (sqrtN - 1) / 2 - 1;

        int nbPrime = 1;    // admit 2 is prime;
        int iSqBit = 3;
        int magicIndexAdd = 8;
        for (int i = 0; i <= sqrtIBit; i++)
        {
            boolean isPrime = !tab[i];
            if (isPrime)
            {
                nbPrime++;
                int x = ((i + 1) << 1) | 0x1;   // <--> x =(i+1)*2 +1;   //if i==2, so x == 7
//                int xSq = x * x;  //sous x*x, les cases multiples de x on deja ete cochees par le parcours d'autre nombres premiers

                //autre propriete remarquable: 3, 5,7,9,11,13,15 ... au carre == 9,25,49,81,121,169,225,289 ...
                // qui ont l'index  3, 11, 23, 39, 59, 83, 111, 143 ...
                // incroyable: a chaque iteration, on a +8, +12, +16, +20, +24, +28, +32... On ajoute 4 a un nombre ajouter precedemment en commencant par 8
                // for i = 2 so x = 7 => iSqBit = 23 so x*x = 49
                for (int j = iSqBit; j < sizeTab; j += x)    //we say that all multiplier of this "x" are not prime numbers.
                {
                    //for x = 7 we change (i=9, i=16,) i=23, i=30 ... i=2+k*7 --> correspond to (x=21, x=35,) x=49, x=63 ... x=7+k*14
                    tab[j] = true; // => is not prime
                }
            }
            iSqBit += magicIndexAdd;
            magicIndexAdd += 4;
        }
        for (int i = sqrtIBit + 1; i < sizeTab; i++)
        {
            boolean isPrime = !tab[i];
            if (isPrime)
            {
                nbPrime++;
            }
        }

//        int[] tabPrimes = new int[nbPrime];
//        tabPrimes[0] = 2;
//        int iPrime = 1;
//        for (int i = 0; i < sizeTab; i++)
//        {
//
//            boolean isPrime = !tab[i];
//            if (isPrime)
//            {
//                int x = ((i + 1) << 1) | 0x1;   //if i==2, so x == 7
//                tabPrimes[iPrime] = x;
//                iPrime++;
//            }
//        }
//        System.out.println("nb of primary numbers = " + nbPrime);
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

    public static int countPrimes(final boolean[] tab, final int nMax)
    {
        if (nMax < 2)
        {
            return 0;
        }

        int nbPrimes = 1;
        for (final boolean isNotPrime : tab)
        {
            nbPrimes += (isNotPrime ? 0 : 1);
        }

        return nbPrimes;
    }

    public static ArrayList<Integer> getListPrimes(final boolean[] tab, final int nMax)
    {
        if (nMax < 2)
        {
            return new ArrayList<Integer>();
        }
        ArrayList<Integer> listPrimes = new ArrayList<>((int) (nMax / Math.log(nMax) * 1.1));
        listPrimes.add(2);

        int x = 3;
        for (final boolean isNotPrime : tab)
        {
            if (!isNotPrime)
            {
                listPrimes.add(x);
            }
            x += 2;
        }
        return listPrimes;
    }
}
