package com.abc.pp.fifo.impl;

import java.lang.reflect.*;
import java.util.*;

import com.abc.pp.fifo.*;

/**
 * Implementation of {@link PPBoundedFifo} which uses a circular array
 * internally.
 * <p>
 * Look at the documentation in PPBoundedFifo to see how the methods are
 * supposed to work.
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
public class CircularArrayPPBoundedFifo<T> implements PPBoundedFifo<T> {
    private final Class<T> itemType;
    private final T[] slots;
    private int head;
    private int tail;
    private int count;
    private final Object lockObject;

    public CircularArrayPPBoundedFifo(int fixedCapacity,
                                    Class<T> itemType,
                                    Object proposedLockObject) {

        lockObject =
            proposedLockObject != null ? proposedLockObject : new Object();

        if ( fixedCapacity < 1 ) {
            throw new IllegalArgumentException(
                "fixedCapacity must be at least 1");
        }

        if (itemType == null) {
            throw new IllegalArgumentException("itemType must not be null");
        }
        this.itemType = itemType;

        slots = createTypeArray(fixedCapacity);
        head = 0;
        tail = 0;
        count = 0;

        // TODO - add more to constructor if you need to....but don't change code above this line
    }

    // this constructor is correct as written - do not change
    public CircularArrayPPBoundedFifo(int fixedCapacity, Class<T> itemType) {
        this(fixedCapacity, itemType, null);
    }

    @SuppressWarnings("unchecked")
    private T[] createTypeArray(int size) {
        T[] array = (T[]) Array.newInstance(itemType, size);
        return array;
    }

    /** Returns the number if items currently in the FIFO. */
    @Override
    public int getCount() {
        synchronized ( lockObject ) {
            return count;
        }
    }

    /** Returns true if {@link #getCount()} == 0. */
    @Override
    public boolean isEmpty() {
        synchronized (lockObject) {
            if (count == 0) {
                return true;
            }
        }
        return false;
    }

    /** Returns true if {@link #getCount()} == {@link #getCapacity()}. */
    @Override
    public boolean isFull() {
        synchronized (lockObject) {
            if (count == slots.length) {
                return true;
            }
        }
        return false;
    }

    /** Removes any and all items in the FIFO leaving it in an empty state. */
    @Override
    public void clear() {
        synchronized ( lockObject ) {
            head = 0;
            tail = 0;
            count = 0;
            lockObject.notifyAll();
            Arrays.fill(slots, null);
        }
    }

    /**
     * Returns the maximum number of items which can be stored in this FIFO.
     * This value never changes.
     */
    @Override
    public int getCapacity() {
        return slots.length;
    }


    /**
     * Add the specified item to the fifo.
     * If currently full, the calling thread waits until there is space and
     * then adds the item.
     * If this method doesn't throw InterruptedException, then the item was
     * successfully added.
     */
    @Override
    public void add(T item) throws InterruptedException {
        synchronized ( lockObject ) {
            boolean notFull = waitWhileFull(0);

            while (waitWhileFull(0)) {
                slots[tail] = item;
                tail = (tail + 1) % slots.length;
                count++;
                lockObject.notifyAll();
                return;
            }
        }
    }


    /**
     * Adds all of the specified items. This call may wait multiple times
     * until all have been added. The number of items to add can exceed the
     * capacity... it just waits until some other thread removes items to make
     * more space.
     * If this method doesn't throw InterruptedException, then all of the items
     * were successfully added.
     */
    @Override
    public void addAll(T[] items) throws InterruptedException {
        synchronized ( lockObject ) {
            for (T item : items) {
                add(item);
            }
        }
    }


    /**
     * Returns a reference to use for synchronized blocks which need to
     * call multiple methods without other threads being able to get in.
     * Never returns null.
     */
    @Override
    public Object getLockObject() {
        return lockObject;
    }

    // this method is correct as written - do not change
    @Override
    public Class<T> getItemType() {
        return itemType;
    }

    /**
     * Removes and returns the next item.
     * If currently empty, the calling thread waits until another thread adds
     * an item.
     * If this method doesn't throw InterruptedException, then the item was
     * successfully removed.
     */
    @Override
    public T remove() throws InterruptedException {
        synchronized ( lockObject ) {
            T result = null;
            while (result == null) {
                waitWhileEmpty(0);
                result = removeItem();
            }
            return result;
        }
    }

    // Helper method to remove without waiting
    private T removeItem() {
        synchronized ( lockObject ) {
            T item = null;
            if (!isEmpty()) {
                item = slots[head];
                head = (head + 1) % slots.length;
                count--;
                lockObject.notifyAll();
            }
            return item;
        }
    }

    /**
     * Removes all the items, waiting until there is at least one to remove.
     * If currently empty, the calling thread waits until another thread adds
     * an item.
     */
    @Override
    public T[] removeAtLeastOne() throws InterruptedException {
        synchronized ( lockObject ) {
            waitWhileEmpty(0);
            return removeAll();
        }
    }

    /**
     * Immediately removes any and all items without waiting.
     * If currently empty, a zero-length array is returned (non-blocking).
     */
    @Override
    public T[] removeAll() {
        List<T> removedItems = new ArrayList<T>();

        if (!isEmpty()) {
            synchronized (lockObject) {
                while (!isEmpty()) {
                    removedItems.add(removeItem());
                }
                lockObject.notifyAll();
            }
        }
        return (T[]) removedItems.toArray();
    }

    /**
     * Waits until the fifo is empty with a timeout.
     * If currently empty, the calling thread returns right away.
     * @param msTimeout approximate maximum number of ms to wait for the fifo
     * to become empty. If this much time elapses and it's still not empty,
     * false is returned. A value of 0 means the waiting should never time out.
     * @return true if empty, false if timed out
     * @throws InterruptedException if interrupted while waiting
     */
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
                    return true;
                }
                msRemaining = endTime - System.currentTimeMillis();
            }
            return false;
        }
    }

    /**
     * Waits until the fifo is empty (no timeout).
     * If currently empty, the calling thread returns right away.
     * @throws InterruptedException if interrupted while waiting
     */
    @Override
    public void waitUntilEmpty() throws InterruptedException {
        waitUntilEmpty(0);
    }

    /**
     * Waits while the fifo is empty with a timeout.
     * If not currently empty, the calling thread returns right away.
     * @param msTimeout approximate maximum number of ms to wait while the fifo
     * is empty. If this much time elapses and it's still empty,
     * false is returned. A value of 0 means the waiting should never time out.
     * @return true if not empty, false if timed out
     * @throws InterruptedException if interrupted while waiting
     */
    @Override
    public boolean waitWhileEmpty(long msTimeout) throws InterruptedException {
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
                    return true;
                }
                msRemaining = endTime - System.currentTimeMillis();
            }
            return false;
        }
    }

    /**
     * Waits while the fifo is empty (no timeout).
     * If not currently empty, the calling thread returns right away.
     * @throws InterruptedException if interrupted while waiting
     */
    @Override
    public void waitWhileEmpty() throws InterruptedException {
        waitWhileEmpty(0);
    }

    /**
     * Waits until the fifo is full with a timeout.
     * If currently full, the calling thread returns right away.
     * @param msTimeout approximate maximum number of ms to wait for the fifo
     * to become full. If this much time elapses and it's still not full,
     * false is returned. A value of 0 means the waiting should never time out.
     * @return true if full, false if timed out
     * @throws InterruptedException if interrupted while waiting
     */
    @Override
    public boolean waitUntilFull(long msTimeout) throws InterruptedException {
        synchronized ( lockObject ) {
            if (!isFull()) return true;

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
                if (!isFull()) {
                    return true;
                }
                msRemaining = endTime - System.currentTimeMillis();
            }
            return false;
        }
    }

    /**
     * Waits until the fifo is full (on timeout).
     * If currently full, the calling thread returns right away.
     * @throws InterruptedException if interrupted while waiting
     */
    @Override
    public void waitUntilFull() throws InterruptedException {
        waitUntilEmpty(0);
    }

    /**
     * Waits until the fifo is full with a timeout.
     * If currently full, the calling thread returns right away.
     * @param msTimeout approximate maximum number of ms to wait for the fifo
     * to become full. If this much time elapses and it's still not full,
     * false is returned. A value of 0 means the waiting should never time out.
     * @return true if full, false if timed out
     * @throws InterruptedException if interrupted while waiting
     */
    @Override
    public boolean waitWhileFull(long msTimeout) throws InterruptedException {
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
                    return true;
                }
                msRemaining = endTime - System.currentTimeMillis();
            }
            return false;
        }
    }

    /**
     * Waits until the fifo is full (on timeout).
     * If currently full, the calling thread returns right away.
     * @throws InterruptedException if interrupted while waiting
     */
    @Override
    public void waitWhileFull() throws InterruptedException {
        waitWhileFull(0);
    }
}
