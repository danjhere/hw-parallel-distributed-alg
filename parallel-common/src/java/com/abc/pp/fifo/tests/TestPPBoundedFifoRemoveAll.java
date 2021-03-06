package com.abc.pp.fifo.tests;

import com.abc.ds.tests.*;
import com.abc.pp.fifo.*;
import com.programix.util.*;

/* deliberate package access */
class TestPPBoundedFifoRemoveAll extends TestPPBoundedFifoBase {
    public TestPPBoundedFifoRemoveAll(PPBoundedFifoFactory factory) {
        super("removeAll()", factory);
    }

    @Override
    protected void performTests() {
        testOnEmpty();
        testOnOne();
        testOnTwo();
        testOnSeveral();
    }

    private void testOnEmpty() {
        outln(" - removeAll() on empty -");
        PPBoundedFifo<String> fifo = createDS();
        outln("isEmpty()", fifo.isEmpty(), true);

        checkRemoveAll(fifo, StringTools.ZERO_LEN_ARRAY);
        outln("isEmpty()", fifo.isEmpty(), true);
    }

    private void testOnOne() {
        outln(" - removeAll() on one -");
        PPBoundedFifo<String> fifo = createDS();
        add(fifo, "apple");
        checkRemoveAll(fifo, "apple");
        outln("isEmpty()", fifo.isEmpty(), true);
    }

    private void testOnTwo() {
        outln(" - removeAll() on two -");
        PPBoundedFifo<String> fifo = createDS();
        add(fifo, "apple");
        add(fifo, "banana");
        checkRemoveAll(fifo, "apple", "banana");
        outln("isEmpty()", fifo.isEmpty(), true);
    }

    private void testOnSeveral() {
        outln(" - removeAll() on several -");
        PPBoundedFifo<String> fifo = createDS();
        outln("adding some junk to be cleared before removeAll() test...");
        add(fifo, "JUNK A");
        add(fifo, "JUNK B");
        add(fifo, "JUNK C");
        outln("clear()...");
        fifo.clear();

        String[] fruits = new TestFruitGenerator(
            TestFruitGenerator.RANDOM_SEED_5).nextRandom(20);

        add(fifo, fruits);
        checkRemoveAll(fifo, fruits);
        outln("isEmpty()", fifo.isEmpty(), true);
    }
}
