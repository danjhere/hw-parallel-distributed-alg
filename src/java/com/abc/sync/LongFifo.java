package com.abc.sync;

public interface LongFifo {
    /** Returns the number if items currently in the FIFO. */
    int getCount();

    /** Returns true if {@link #getCount()} == 0. */
    boolean isEmpty();

    /** Returns true if {@link #getCount()} == {@link #getCapacity()}. */
    boolean isFull();

    /** Removes any and all items in the FIFO leaving it in an empty state. */
    void clear();

    /**
     * Returns the maximum number of items which can be stored in this FIFO.
     * This value never changes.
     */
    int getCapacity();

    /**
     * Adds the specified specified value. Possibly wait until not full.
     */
    void add(long value) throws InterruptedException;

    /**
     * Adds the specified value only waiting msTimeout ms while/if full.
     * Returns true, if add successful.
     * Returns false, if timed out.
     */
    boolean add(long value, long msTimeout) throws InterruptedException;

    /**
     * Removes and returns the next item. Possilby wait until not empty.
     */
    long remove() throws InterruptedException;

    public void waitUntilEmpty() throws InterruptedException;
    public boolean waitUntilEmpty(long msTimeout) throws InterruptedException;

    /**
     * Returns a reference to use for synchronized blocks which need to
     * call multiple methods without other threads being able to get in.
     * Never returns null.
     */
    Object getLockObject();
}
