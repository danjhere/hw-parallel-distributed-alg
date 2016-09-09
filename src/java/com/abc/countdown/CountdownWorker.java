package com.abc.countdown;

public class CountdownWorker {


    private static final long SLEEP_TIME_MILLIS = 1000;

    private final int totalSeconds;
    private final MinuteSecondDisplay display;

    private Thread internalThread;
    private long startTimeMillis;

    public CountdownWorker(int totalSeconds, MinuteSecondDisplay display) {
        this.totalSeconds = totalSeconds;
        this.display = display;

        internalThread = new Thread( new Runnable() {
            @Override
            public void run() {
                doWork();
            }
        });
        internalThread.start();
    }

    private void doWork() {
        try {
            this.startTimeMillis = System.currentTimeMillis();
            int i = totalSeconds;
            while (i > 0) {
                display.setSeconds(i);
                Thread.sleep(getSleepTime(totalSeconds - i));
                i--;
            }
            display.setSeconds(i);
            System.out.println(System.currentTimeMillis() - this.startTimeMillis);
        } catch (InterruptedException e) {
            System.err.println("InterruptedException " + e.getMessage());
        }

    }

    private long getSleepTime(int i) {
        long expectedTime = this.startTimeMillis + i * SLEEP_TIME_MILLIS;
        long difference = System.currentTimeMillis() - expectedTime;
        return SLEEP_TIME_MILLIS - difference;
    }
}
