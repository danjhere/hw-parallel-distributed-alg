package com.abc.sync;


public class Demo {
    private static void simpleCheck() throws InterruptedException {
        LongFifo fifo = new CircularArrayLongFifo(5);

        // expect: empty->true, full->false, count->0
        System.out.println("fifo.isEmpty()=" + fifo.isEmpty());
        System.out.println("fifo.isFull()=" + fifo.isFull());
        System.out.println("fifo.getCount()=" + fifo.getCount());

        fifo.add(5);
        fifo.add(7);
        fifo.add(3);

        // expect: empty->false, full->false, count->3
        System.out.println("fifo.isEmpty()=" + fifo.isEmpty());
        System.out.println("fifo.isFull()=" + fifo.isFull());
        System.out.println("fifo.getCount()=" + fifo.getCount());

        System.out.println("fifo.remove()=" + fifo.remove());
        System.out.println("fifo.remove()=" + fifo.remove());
        System.out.println("fifo.remove()=" + fifo.remove());

        // expect: empty->true, full->false, count->0
        System.out.println("fifo.isEmpty()=" + fifo.isEmpty());
        System.out.println("fifo.isFull()=" + fifo.isFull());
        System.out.println("fifo.getCount()=" + fifo.getCount());
    }

    public static void main(String[] args) {
        try {
            simpleCheck();
        } catch ( InterruptedException x ) {
            x.printStackTrace();
        }
    }
}

