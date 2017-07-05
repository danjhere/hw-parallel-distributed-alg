package com.abc.pp.fifo.tests;

import com.abc.pp.fifo.*;
import com.programix.testing.*;
import com.programix.thread.*;

/* deliberate package access */
class TestPPBoundedFifoRemoveWhenInitiallyEmpty extends TestPPBoundedFifoBase {
    public TestPPBoundedFifoRemoveWhenInitiallyEmpty(PPBoundedFifoFactory factory,
                                                     TestThreadFactory threadFactory) {

        super("remove() - when initially empty", factory, threadFactory);
    }

    @Override
    protected void performTests() {
        try {
            testOne();
        } catch ( Exception x ) {
            failureExceptionWithStackTrace(x);
        }
    }

    private void testOne() throws InterruptedException {
        Adder<String> adder = null;
        try {
            PPBoundedFifo<String> fifo = createDS();
            outln("count=" + fifo.getCount() + ", isEmpty=" + fifo.isEmpty() + ", isFull=" + fifo.isFull());

            String[] fruits = getFruits(3);

            adder = new Adder<>(fifo, 1000, 1000);
            adder.appendItemsToBeAdded(fruits);

            double secExpectedWaitingTime = 1.0;
            double tolerance = Math.max(0.02, secExpectedWaitingTime * 0.10);

            testAccess.outln("Attempting to call remove()...");
            NanoTimer timer = NanoTimer.createStarted();
            String removedItem = fifo.remove();
            timer.stop();
            testAccess.outln("removed item", removedItem, fruits[0]);
            testAccess.outln("...finished remove(), seconds of wait time: ", timer.getElapsedSeconds(), secExpectedWaitingTime, tolerance, 5);
        } finally {
            if (adder != null) {
                adder.stopRequest();
                adder.waitUntilDone(2000);
            }
        }
    }
}
