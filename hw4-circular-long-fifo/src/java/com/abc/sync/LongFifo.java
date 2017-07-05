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
     * Add if possible. If false is returned, the add was not possible (full).
     * If true is returned, the add was successful.
     */
    boolean add(long value);

    /**
     * Removes and returns the next item if there is one.
     * This method never returns null.
     * Check the isValid() method of RemoveResult: if true, then the remove
     * was successful. If false, the remove was not possible (empty).
     */
    RemoveResult remove();

    /**
     * Returns a reference to use for synchronized blocks which need to
     * call multiple methods without other threads being able to get in.
     * Never returns null.
     */
    Object getLockObject();

    /**
     * Used to allow callers of {@link #remove()} to see if a value was actually
     * removed or not.
     * Check {@link #isValid()} and only call {@link #getValue()} if true.
     * Instances are immutable.
     */
    public static final class RemoveResult {
        /**
         * A shared, immutable instance which is never valid.
         */
        public static final RemoveResult INVALID = new RemoveResult(false, 0);

        private final boolean valid;
        private final long value;

        private RemoveResult(boolean valid, long value) {
            this.valid = valid;
            this.value = value;
        }

        public static RemoveResult createValid(long value) {
            return new RemoveResult(true, value);
        }

        public static RemoveResult createInvalid() {
            return INVALID;
        }

        /**
         * Returns true if {@link #getValue()} holds the value of the removed
         * item. If false, then getValue() cannot be called - nothing was
         * removed.
         */
        public boolean isValid() {
            return valid;
        }

        /**
         * Returns the value which was removed.
         * Only call this method if {@link #isValid()} returns true.
         * @throws IllegalStateException if there is no valid value.
         */
        public long getValue() throws IllegalStateException {
            if (!valid) {
                throw new IllegalStateException(
                    "invalid - cannot call getValue()");
            }
            return value;
        }
    } // type RemoveResult
}
