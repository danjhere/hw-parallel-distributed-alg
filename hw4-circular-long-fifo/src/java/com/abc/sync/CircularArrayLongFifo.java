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
    public CircularArrayLongFifo(int fixedCapacity, Object proposedLockObject) {

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

    @Override
    public boolean isEmpty() {
        return getCount() == 0;
    }

    @Override
    public boolean isFull() {
        return getCount() == getCapacity();
    }

    @Override
    public void clear() {
        synchronized ( lockObject ) {
            for (int i=0; i<slots.length; i++) {
                slots[i] = 0;
            }
            head=0;
            tail=0;
            count=0;
        }
    }

    @Override
    public int getCapacity() {
        // The length is fixed at construction, so this value does not change
        return this.slots.length;
    }

    @Override
    public boolean add(long value) {
        boolean added = false;
        synchronized ( lockObject ) {
            if ( !isFull() ) {
                slots[tail] = value;
                tail = incrementCircular(tail);
                count++;
                added = true;
            }
        }
        return added;
    }

    @Override
    public LongFifo.RemoveResult remove() {
        LongFifo.RemoveResult result = null;
        if ( !isEmpty() ) {
            synchronized ( lockObject ) {
                result = LongFifo.RemoveResult.createValid(slots[head]);
                count--;
                head = incrementCircular(head);
            }
        } else {
            result = LongFifo.RemoveResult.createInvalid();
        }
        return result;
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
