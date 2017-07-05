package com.abc.sync;


public class Demo {
    private static void emptyCheck() throws InterruptedException {
        LongFifo fifo = new CircularArrayLongFifo(5);

//        fifo.add(7);
//        fifo.remove();

        fifo.add(10);
        fifo.add(20);
        fifo.add(30);

        new Remover(fifo, 1000);

        long nsStartTime = System.nanoTime();
        fifo.waitUntilEmpty();
        long nsElapsed = System.nanoTime() - nsStartTime;
        System.out.println(String.format("Yay! It's empty! - after waiting %.5f seconds", nsElapsed / 1e9));
    }

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
            emptyCheck();
        } catch ( InterruptedException x ) {
            x.printStackTrace();
        }
    }

    private static class Remover implements Runnable {
        private final LongFifo fifo;
        private final long msDelayBetweenRemoveAttempts;

        public Remover(LongFifo fifo, long msDelayBetweenRemoveAttempts) {
            this.fifo = fifo;
            this.msDelayBetweenRemoveAttempts = msDelayBetweenRemoveAttempts;

            Thread t = new Thread(this, "Remover");
            t.start();
        }

        @Override
        public void run() {
            try {
                while (true) {
                    System.out.println("[REMOVER] fifo.remove()=" + fifo.remove());
                    Thread.sleep(msDelayBetweenRemoveAttempts);
                }
            } catch ( InterruptedException x ) {
                // ignore
            }
        }
    } // type Remover
}

