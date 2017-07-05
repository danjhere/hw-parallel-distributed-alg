package com.abc.sleep;

public class SleepDrifting {
    private static void correcting() {
        try {
            long msStart = System.currentTimeMillis();


            int msNormalSleepTime = 10;
            for ( int i = 0; i < 500; i++ ) {
                long msExpect = msStart + i * msNormalSleepTime;
                long msActual = System.currentTimeMillis();
                long msToSubtract = msActual - msExpect;
                long msNextSleepTime = msNormalSleepTime - msToSubtract;
                System.out.println("msNextSleepTime=" + msNextSleepTime);
                if (msNextSleepTime > 0) {
                    Thread.sleep(msNextSleepTime);
                }
            }

            long msEnd = System.currentTimeMillis();
            long msElapsed = msEnd - msStart;
            System.out.println("msElapsed=" + msElapsed);
        } catch (InterruptedException x) {
            // ignore and return
        }
    }

    private static void correctingNanos() {
        try {
            long nsStart = System.nanoTime();
            long nsNormalSleepTime = 10 * 1000000L;
            for ( int i = 0; i < 500; i++ ) {
                long nsExpect = nsStart + i * nsNormalSleepTime;
                long nsActual = System.nanoTime();
                long nsToSubtract = nsActual - nsExpect;
                long nsNextSleepTime = nsNormalSleepTime - nsToSubtract;
                System.out.println("nsNextSleepTime=" + nsNextSleepTime);
                if (nsNextSleepTime > 0) {
                    long msToSleep = nsNextSleepTime / 1000000;
                    int nsToSleep = (int) (nsNextSleepTime % 1000000);
                    //System.out.println("msToSleep=" + msToSleep + ", nsToSleep=" + nsToSleep);
                    Thread.sleep(msToSleep, nsToSleep);
                }
            }

            long nsEnd = System.nanoTime();
            long nsElapsed = nsEnd - nsStart;
            System.out.println("nsElapsed=" + nsElapsed);
            System.out.println(String.format("seconds %12.9f", nsElapsed / 1e9));
        } catch (InterruptedException x) {
            // ignore and return
        }
    }

    public static void main(String[] args) {
//        long msStart = System.currentTimeMillis();
//        // # of ms since Jan 01 00:00:00.000 1970 in Greenwich England
//
//        System.out.println("currentTimeInMilliseconds=" + msStart);
//
//        try {
//            Thread.sleep(1000);
//        } catch ( InterruptedException x ) {
//            x.printStackTrace();
//        }
//
//        long msEnd = System.currentTimeMillis();
//        long msElapsed = msEnd - msStart;
//        System.out.println("msElapsed=" + msElapsed);
//
//        correcting();
        correctingNanos();

    }
}
