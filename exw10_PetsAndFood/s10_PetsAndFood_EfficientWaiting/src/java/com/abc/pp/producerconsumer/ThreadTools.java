package com.abc.pp.producerconsumer;

public class ThreadTools {
    public static final boolean SUCCESS = true;
    public static final boolean TIMED_OUT = false;

    // no instances
    private ThreadTools() {

    }

    public static void outln(String fmt, Object... params) {
        String msg = String.format(fmt, params);
        System.out.printf("%-10.10s|%s%n",
            Thread.currentThread().getName(),
            msg);
    }
}
