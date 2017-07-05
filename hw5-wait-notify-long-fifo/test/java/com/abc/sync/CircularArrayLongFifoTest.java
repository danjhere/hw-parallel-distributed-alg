package com.abc.sync;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * Created by marymosman on 9/22/16.
 */
public class CircularArrayLongFifoTest {

    @Test
    public void incrementCircular() {
        CircularArrayLongFifo fifo = new CircularArrayLongFifo(4);
        assertEquals(0, fifo.incrementCircular(3));
        assertEquals(3, fifo.incrementCircular(2));
        assertEquals(2, fifo.incrementCircular(1));
        assertEquals(1, fifo.incrementCircular(0));
    }


    @Test
    public void add_notFull() throws InterruptedException {
        CircularArrayLongFifo fifo = new CircularArrayLongFifo(2);
        fifo.add(1);
        assertEquals(1, fifo.getCount());
        fifo.add(2);
        assertEquals(2, fifo.getCount());
    }

    @Test
    public void add_full_timeout() throws InterruptedException {
        CircularArrayLongFifo fifo = new CircularArrayLongFifo(1);
        fifo.add(1);
        assertEquals(1, fifo.getCount());
        assertEquals(false, fifo.add(2, 1000));
        assertEquals(1, fifo.getCount());
        assertEquals(1L, fifo.remove());
    }


    @Test
    public void add_full_wait() throws InterruptedException {

        long item1 = 100L;
        long item2 = 100L;

        // Fill fifo
        CircularArrayLongFifo fifo = new CircularArrayLongFifo(1);
        fifo.add(item1);


        Thread full = new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    fifo.add(item2);
                    assertEquals(item2, fifo.remove());
                } catch (Exception e) {
                    fail("Unexpected exception caught: " + e.getMessage());
                }
            }
        });
        full.start();

        Thread remove = new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    assertEquals(item1, fifo.remove());
                } catch (Exception e) {
                    fail("Unexpected exception caught: " + e.getMessage());
                }
            }
        });
        remove.start();
    }


    @Test
    public void notify_on_clear() throws InterruptedException {

        long item1 = 100L;
        long item2 = 100L;

        // Fill fifo
        CircularArrayLongFifo fifo = new CircularArrayLongFifo(1);
        fifo.add(item1);


        Thread full = new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    fifo.add(item2);
                    assertEquals(item2, fifo.remove());
                } catch (Exception e) {
                    fail("Unexpected exception caught: " + e.getMessage());
                }
            }
        });
        full.start();

        Thread remove = new Thread( new Runnable() {
            @Override
            public void run() {
                fifo.clear();
            }
        });
        remove.start();
    }

    @Test
    public void remove_notEmpty() throws InterruptedException {
        CircularArrayLongFifo fifo = new CircularArrayLongFifo(2);
        fifo.add(1);
        fifo.add(2);
        assertEquals(2, fifo.getCount());
        assertEquals(1L, fifo.remove());
        assertEquals(1, fifo.getCount());
        assertEquals(2L, fifo.remove());
        assertEquals(0, fifo.getCount());
    }

    @Test
    public void remove_empty() {

        // Empty fifo
        CircularArrayLongFifo fifo = new CircularArrayLongFifo(1);
        long item = 100L;

        Thread empty = new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    assertEquals(item, fifo.remove());
                } catch (Exception e) {
                    fail("Unexpected exception caught: " + e.getMessage());
                }
            }
        });
        empty.start();

        Thread add = new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    fifo.add(item);
                } catch (Exception e) {
                    fail("Unexpected exception caught: " + e.getMessage());
                }
            }
        });
        add.start();
    }

    @Test
    public void waitUntilEmpty() throws InterruptedException {

        // Full fifo
        CircularArrayLongFifo fifo = new CircularArrayLongFifo(1);
        fifo.add(100L);

        Thread full = new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    fifo.waitUntilEmpty();
                    assertEquals(true, true);
                } catch (Exception e) {
                    fail("Unexpected exception caught: " + e.getMessage());
                }
            }
        });
        full.start();

        Thread remove = new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    fifo.remove();
                } catch (Exception e) {
                    fail("Unexpected exception caught: " + e.getMessage());
                }
            }
        });
        remove.start();
    }

    @Test
    public void waitUntilEmpty_timeout1() throws InterruptedException {

        // Full fifo
        CircularArrayLongFifo fifo = new CircularArrayLongFifo(1);
        fifo.add(100L);

        Thread full = new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    assertTrue(fifo.waitUntilEmpty(5000));
                } catch (Exception e) {
                    fail("Unexpected exception caught: " + e.getMessage());
                }
            }
        });
        full.start();

        Thread remove = new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    fifo.remove();
                } catch (Exception e) {
                    fail("Unexpected exception caught: " + e.getMessage());
                }
            }
        });
        remove.start();
    }

    @Test
    public void waitUntilEmpty_timeout2() throws InterruptedException {

        // Full fifo
        CircularArrayLongFifo fifo = new CircularArrayLongFifo(1);
        fifo.add(100L);

        Thread full = new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    assertFalse(fifo.waitUntilEmpty(5000));
                } catch (Exception e) {
                    fail("Unexpected exception caught: " + e.getMessage());
                }
            }
        });
        full.start();
    }

    @Test
    public void getCount()  throws InterruptedException {
        CircularArrayLongFifo fifo = new CircularArrayLongFifo(2);
        assertEquals(0, fifo.getCount());
        fifo.add(1);
        assertEquals(1, fifo.getCount());
    }

    @Test
    public void isEmpty() throws InterruptedException {
        CircularArrayLongFifo fifo = new CircularArrayLongFifo(2);

        assertEquals(true, fifo.isEmpty());
        fifo.add(1);
        assertEquals(false, fifo.isEmpty());
    }

    @Test
    public void isFull() throws InterruptedException {
        CircularArrayLongFifo fifo = new CircularArrayLongFifo(1);

        fifo.add(1);
        assertEquals(1, fifo.getCount());
        assertEquals(true, fifo.isFull());
    }

    @Test
    public void clear() throws InterruptedException {
        CircularArrayLongFifo fifo = new CircularArrayLongFifo(2);
        fifo.add(1);
        fifo.add(2);
        assertEquals(2, fifo.getCount());

        fifo.clear();
        assertEquals(0, fifo.getCount());
        assertEquals(true, fifo.isEmpty());
    }

    @Test
    public void circularAddTest() throws InterruptedException {
        CircularArrayLongFifo fifo = new CircularArrayLongFifo(2);

        fifo.add(1);
        assertEquals(1, fifo.remove());
        fifo.add(2);
        assertEquals(2, fifo.remove());
        fifo.add(3);
        fifo.add(4);
        assertEquals(3, fifo.remove());
        assertEquals(4, fifo.remove());
    }

}
