package com.abc.pp.prime;

public class PrimeTools {
    // no instances
    private PrimeTools() {
    }

    public static boolean isPrime(long n) {
        for ( int divisor = 2; divisor < n; divisor++ ) {
            if (n % divisor == 0) {
                return false;
            }
        }
        return true;
    }
}
