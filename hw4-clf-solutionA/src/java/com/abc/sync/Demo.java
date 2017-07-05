package com.abc.sync;

import com.abc.sync.LongFifo.RemoveResult;

public class Demo {
    private static void simpleCheck() {
//        LongFifo fifo = new CircularArrayLongFifo(5);

        String mySpecialThing = "SDfsdfsF";
        LongFifo fifo = new CircularArrayLongFifo(5, mySpecialThing);

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

        RemoveResult rrA = fifo.remove();
        if (rrA.isValid()) {
            long a = rrA.getValue();
            System.out.println("a=" + a);
        }
        RemoveResult rrB = fifo.remove();
        if (rrB.isValid()) {
            long b = rrB.getValue();
            System.out.println("b=" + b);
        }
        RemoveResult rrC = fifo.remove();
        if (rrC.isValid()) {
            long c = rrC.getValue();
            System.out.println("c=" + c);
        }

        // expect: empty->true, full->false, count->0
        System.out.println("fifo.isEmpty()=" + fifo.isEmpty());
        System.out.println("fifo.isFull()=" + fifo.isFull());
        System.out.println("fifo.getCount()=" + fifo.getCount());

        synchronized ( fifo.getLockObject() ) {
            if (fifo.isFull()) {
                fifo.remove();
            }
        }


    }

    public static void main(String[] args) {
        simpleCheck();
    }
}

