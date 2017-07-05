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
    public void add_notFull() {
        CircularArrayLongFifo fifo = new CircularArrayLongFifo(2);
        assertEquals(true, fifo.add(1));
        assertEquals(1, fifo.getCount());
        assertEquals(true, fifo.add(2));
        assertEquals(2, fifo.getCount());
    }

    @Test
    public void add_full() {
        CircularArrayLongFifo fifo = new CircularArrayLongFifo(1);
        assertEquals(true, fifo.add(1));
        assertEquals(1, fifo.getCount());
        assertEquals(false, fifo.add(2));
        assertEquals(1, fifo.getCount());
        assertEquals(1L, fifo.remove().getValue());
    }

    @Test
    public void remove_notEmpty() {
        CircularArrayLongFifo fifo = new CircularArrayLongFifo(2);
        fifo.add(1);
        fifo.add(2);
        assertEquals(2, fifo.getCount());
        assertEquals(1L, fifo.remove().getValue());
        assertEquals(1, fifo.getCount());
        assertEquals(2L, fifo.remove().getValue());
        assertEquals(0, fifo.getCount());
    }

    @Test
    public void remove_empty() {
        CircularArrayLongFifo fifo = new CircularArrayLongFifo(1);
        assertEquals(LongFifo.RemoveResult.INVALID, fifo.remove());
    }

    @Test
    public void getCount() {
        CircularArrayLongFifo fifo = new CircularArrayLongFifo(2);
        assertEquals(0, fifo.getCount());
        fifo.add(1);
        assertEquals(1, fifo.getCount());
    }

    @Test
    public void isEmpty() {
        CircularArrayLongFifo fifo = new CircularArrayLongFifo(2);

        assertEquals(true, fifo.isEmpty());
        assertEquals(true, fifo.add(1));
        assertEquals(false, fifo.isEmpty());
    }

    @Test
    public void isFull() {
        CircularArrayLongFifo fifo = new CircularArrayLongFifo(1);

        assertEquals(true, fifo.add(1));
        assertEquals(1, fifo.getCount());
        assertEquals(true, fifo.isFull());
    }

    @Test
    public void clear() {
        CircularArrayLongFifo fifo = new CircularArrayLongFifo(2);
        assertEquals(true, fifo.add(1));
        assertEquals(true, fifo.add(2));
        assertEquals(2, fifo.getCount());

        fifo.clear();
        assertEquals(0, fifo.getCount());
        assertEquals(true, fifo.isEmpty());
    }

    @Test
    public void circularAddTest() {
        CircularArrayLongFifo fifo = new CircularArrayLongFifo(2);

        fifo.add(1);
        assertEquals(1, fifo.remove().getValue());
        fifo.add(2);
        assertEquals(2, fifo.remove().getValue());
        fifo.add(3);
        fifo.add(4);
        assertEquals(3, fifo.remove().getValue());
        assertEquals(4, fifo.remove().getValue());
    }

}
