package com.abc.pp.producerconsumer;

public class ThreadTools {

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
