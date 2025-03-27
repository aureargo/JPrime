/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utility;

public class Divisors
{

    public static void main(String[] args)
    {
        long val = 76072363;
        if (args.length != 0)
        {
            String str = args[0];
            val = Long.parseLong(str);
        }
        findDivisors(val);
    }

    private static void findDivisors(long v)
    {
        long rest = v;
        while ((rest & 0b1) == 0b0) //%2
        {
            System.out.println(2);
            rest >>>= 1;
        }

        long div = 3;
        while (div * div <= rest)
        {
            while (rest % div == 0)
            {
                System.out.println(div);
                rest /= div;
            }
            div += 2;
        }
        System.out.println(rest);
    }

    private static void findDivisors(int v)
    {
        int rest = v;
        while ((rest & 0b1) == 0b0) //%2
        {
            System.out.println(2);
            rest >>>= 1;
        }

        int div = 3;
        while (div * div <= rest)
        {
            while (rest % div == 0)
            {
                System.out.println(div);
                rest /= div;
            }
            div += 2;
        }
        System.out.println(rest);
    }
}
