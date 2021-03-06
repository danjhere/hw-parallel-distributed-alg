package com.abc.sync;


import java.util.Arrays;

/**
 * Implementation of {@link LongFifo} which uses a circular array internally.
 * <p>
 * Look at the documentation in LongFifo to see how the methods are supposed to
 * work.
 * <p>
 * The data is stored in the slots array. count is the number of items currently
 * in the FIFO. When the FIFO is not empty, head is the index of the next item
 * to remove. When the FIFO is not full, tail is the index of the next available
 * slot to use for an added item. Both head and tail need to jump to index 0
 * when they "increment" past the last valid index of slots (this is what makes
 * it circular).
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/Circular_buffer">Circular Buffer
 * on Wikipedia</a> for more information.
 */
public class CircularArrayLongFifo implements LongFifo {
    // do not change any of these fields:
    private final long[] slots;
    private int head;
    private int tail;
    private int count;
    private final Object lockObject;

    // this constructor is correct as written - do not change
    public CircularArrayLongFifo(int fixedCapacity,
                                 Object proposedLockObject) {

        lockObject = proposedLockObject != null ? proposedLockObject : new Object();

        slots = new long[fixedCapacity];
        head = 0;
        tail = 0;
        count = 0;
    }

    // this constructor is correct as written - do not change
    public CircularArrayLongFifo(int fixedCapacity) {
        this(fixedCapacity, null);
    }

    // this method is correct as written - do not change
    @Override
    public int getCount() {
        synchronized ( lockObject ) {
            return count;
        }
    }

    // this method is correct as written - do not change
    @Override
    public boolean isEmpty() {
        synchronized ( lockObject ) {
            return count == 0;
        }
    }

    // this method is correct as written - do not change
    @Override
    public boolean isFull() {
        synchronized ( lockObject ) {
            return count == slots.length;
        }
    }

    @Override
    public void clear() {
        synchronized ( lockObject ) {
            Arrays.fill(this.slots, 0);
            head=0;
            tail=0;
            count=0;
            lockObject.notifyAll();
        }
    }

    // this method is correct as written - do not change
    @Override
    public int getCapacity() {
        return slots.length;
    }

    @Override
    public boolean add(long value, long msTimeout) throws InterruptedException {
        boolean added = false;
        long endTime = System.currentTimeMillis() + msTimeout;
        long msRemaining = msTimeout;

        synchronized ( lockObject ) {

            while ( !isFull() && msRemaining > 0 ) {
                lockObject.wait(msTimeout);
                msRemaining = endTime - System.currentTimeMillis();
            }

            if ( !isFull() ) {
                slots[tail] = value;
                tail = incrementCircular(tail);
                count++;
                added = true;
                lockObject.notifyAll();
            }
        }
        return added;
    }

    @Override
    public void add(long value) throws InterruptedException {
        this.add(value, 0);
    }

    @Override
    public long remove() throws InterruptedException {

        synchronized ( lockObject ) {
            while (isEmpty()) {
                lockObject.wait();
            }

            long result = slots[head];
            count--;
            head = incrementCircular(head);
            return result;
        }
    }

    @Override
    public boolean waitUntilEmpty(long msTimeout) throws InterruptedException {
        long startTimeMillis = System.currentTimeMillis();
        long msElapsed = 0;

        synchronized ( lockObject ) {
            while (!isEmpty() && msElapsed >= 0) {
                lockObject.wait(msTimeout);
                msElapsed = System.currentTimeMillis() - startTimeMillis;
            }
            return this.isEmpty();
        }
    }

    @Override
    public void waitUntilEmpty() throws InterruptedException {
        this.waitUntilEmpty(0);
    }

    // this method is correct as written - do not change
    @Override
    public Object getLockObject() {
        return lockObject;
    }

    int incrementCircular(int index) {
        int newIndex = index + 1;
        if (newIndex == getCapacity()) {
            newIndex = 0;
        }
        return newIndex;
    }
}
