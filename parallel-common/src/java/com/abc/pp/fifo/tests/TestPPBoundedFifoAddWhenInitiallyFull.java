package com.abc.pp.fifo.tests;

import com.abc.pp.fifo.*;
import com.programix.testing.*;
import com.programix.thread.*;
import com.programix.util.*;

/* deliberate package access */
class TestPPBoundedFifoAddWhenInitiallyFull extends TestPPBoundedFifoBase {
    public TestPPBoundedFifoAddWhenInitiallyFull(PPBoundedFifoFactory factory,
                                                     TestThreadFactory threadFactory) {

        super("add() - when initially full", factory, threadFactory);
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
        Remover<String> remover = null;
        try {
            PPBoundedFifo<String> fifo = createDS(10, new Object());
            addBulk(fifo, getFruits(10));

            outln("count=" + fifo.getCount() + ", isEmpty=" + fifo.isEmpty() + ", isFull=" + fifo.isFull());

            String[] fruits = getFruits(10, 3);

            remover = new Remover<>(fifo, 1000, 1000);
            remover.appendExpectedItemsToBeRemoved(getFruits(13));

            double secExpectedWaitingTime = 1.0;
            double tolerance = Math.max(0.02, secExpectedWaitingTime * 0.10);

            for ( String fruit : fruits ) {
                testAccess.outln("Attempting to call add(" + StringTools.quoteWrap(fruit) + ")...");
                NanoTimer timer = NanoTimer.createStarted();
                fifo.add(fruit);
                timer.stop();
                testAccess.outln("...finished add(), seconds of wait time: ", timer.getElapsedSeconds(), secExpectedWaitingTime, tolerance, 5);
            }

            remover.waitUntilAllExpectedItemsHaveBeenRemoved(60000);
            Thread.sleep(200);
        } finally {
            if (remover != null) {
                remover.stopRequest();
                remover.waitUntilDone(2000);
            }
        }
    }
}
