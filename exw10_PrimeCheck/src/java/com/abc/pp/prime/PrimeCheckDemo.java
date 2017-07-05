package com.abc.pp.prime;

public class PrimeCheckDemo {
    public static void main(String[] args) {
        long nsStart = System.nanoTime();

        long start = 2;
        long limit = 100;
        System.out.printf("Checking for prime numbers between %,d and %,d%n", start, limit);
        long primeCount = 0;
        for ( long candidate = start; candidate <= limit; candidate++ ) {
            if (PrimeTools.isPrime(candidate)) {
                primeCount++;
                System.out.println(candidate);
            }
        }

        long nsElapsed = System.nanoTime() - nsStart;
        System.out.printf("Found %,d prime numbers between %,d and %,d in %.5f seconds%n",
            primeCount, start, limit, nsElapsed / 1e9);
    }
}
