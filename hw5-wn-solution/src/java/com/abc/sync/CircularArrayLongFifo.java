package com.abc.sync;


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

        lockObject =
            proposedLockObject != null ? proposedLockObject : new Object();

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
            head = 0;
            tail = 0;
            count = 0;
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
        synchronized ( lockObject ) {
            boolean notFull = waitWhileFull(msTimeout);
            if (notFull) {
                slots[tail] = value;
                tail = (tail + 1) % slots.length;
                count++;
                lockObject.notifyAll();
                return true; // success
            } else {
                return false; // timed out
            }
        }
    }

    @Override
    public void add(long value) throws InterruptedException {
        add(value, 0);
    }

    @Override
    public long remove() throws InterruptedException {
        synchronized ( lockObject ) {
            waitWhileEmpty(0);
            long item = slots[head];
            head = (head + 1) % slots.length;
            count--;
            lockObject.notifyAll();
            return item;
        }
    }

    @Override
    public boolean waitUntilEmpty(long msTimeout) throws InterruptedException {
        synchronized ( lockObject ) {
            if (isEmpty()) return true;

            if (msTimeout == 0) {
                do {
                    lockObject.wait();
                } while (!isEmpty());
                return true;
            }

            long endTime = System.currentTimeMillis() + msTimeout;
            long msRemaining = msTimeout;
            while (msRemaining > 0) {
                lockObject.wait(msRemaining);
                if (isEmpty()) {
                    return true; // success
                }
                msRemaining = endTime - System.currentTimeMillis();
            }
            return false; // timed out
        }
    }

    @Override
    public void waitUntilEmpty() throws InterruptedException {
        waitUntilEmpty(0);
    }

    // returns true if no longer empty, false if we timeout
    private boolean waitWhileEmpty(long msTimeout) throws InterruptedException {
        synchronized ( lockObject ) {
            if (!isEmpty()) return true;

            if (msTimeout == 0) {
                do {
                    lockObject.wait();
                } while (isEmpty());
                return true;
            }

            long endTime = System.currentTimeMillis() + msTimeout;
            long msRemaining = msTimeout;
            while (msRemaining > 0) {
                lockObject.wait(msRemaining);
                if (!isEmpty()) {
                    return true; // success
                }
                msRemaining = endTime - System.currentTimeMillis();
            }
            return false; // timed out
        }
    }

    // returns true if no longer full, false if we timeout
    private boolean waitWhileFull(long msTimeout) throws InterruptedException {
        synchronized ( lockObject ) {
            if (!isFull()) return true;

            if (msTimeout == 0) {
                do {
                    lockObject.wait();
                } while (isFull());
                return true;
            }

            long endTime = System.currentTimeMillis() + msTimeout;
            long msRemaining = msTimeout;
            while (msRemaining > 0) {
                lockObject.wait(msRemaining);
                if (!isFull()) {
                    return true; // success
                }
                msRemaining = endTime - System.currentTimeMillis();
            }
            return false; // timed out
        }
    }

    // this method is correct as written - do not change
    @Override
    public Object getLockObject() {
        return lockObject;
    }
}
