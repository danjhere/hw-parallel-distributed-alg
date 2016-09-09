package com.abc.countdown;

public class CountdownWorker {
    private final int totalSeconds;
    private final MinuteSecondDisplay display;

    public CountdownWorker(int totalSeconds, MinuteSecondDisplay display) {
        this.totalSeconds = totalSeconds;
        this.display = display;

        // do the self-run pattern to spawn a thread to do the counting down...
        // ...

    }

    // TODO - add methods to do the counting down and time correction....
    //        call: display.setSeconds(...

}
