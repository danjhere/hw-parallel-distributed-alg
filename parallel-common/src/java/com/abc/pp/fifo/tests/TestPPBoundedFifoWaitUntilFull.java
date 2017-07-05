package com.abc.pp.fifo.tests;

import com.abc.ds.tests.*;
import com.abc.pp.fifo.*;
import com.programix.testing.*;

/* deliberate package access */
class TestPPBoundedFifoWaitUntilFull extends TestPPBoundedFifoBase {
    public TestPPBoundedFifoWaitUntilFull(PPBoundedFifoFactory factory,
                                           TestThreadFactory threadFactory) {
        super("waitUntilFull", factory, threadFactory);
    }

    @Override
    protected void performTests() {
        testWaitingToBeMet();
        testWaitingAlreadyMet();
    }

    private void testWaitingToBeMet() {
        try {
            outln("***** Testing: waitUntilFull - when initially not full *****");

            TestWaitHelper.Callback callback = new AbstractCallback() {
                @Override
                public TestWaitHelper.Callback.Control createNew(Object lockObject) {
                    String[] fruitsInitial = getFruits(8);
                    String[] fruitsForAdder = getFruits(8, 2);

                    fifo = createDS(10, lockObject);
                    addBulk(fifo, fruitsInitial);

                    outln("count=" + fifo.getCount() + ", isEmpty=" + fifo.isEmpty() + ", isFull=" + fifo.isFull());

                    final Adder<String> adder = new Adder<>(fifo, 1000, 1000);
                    adder.appendItemsToBeAdded(fruitsForAdder);

                    return new TestWaitHelper.Callback.Control() {
                        @Override
                        public void stopRequest() {
                            adder.stopRequest();
                        }

                        @Override
                        public boolean waitUntilDone(long msTimeout) throws InterruptedException {
                            return adder.waitUntilDone(msTimeout);
                        }
                    };
                }

                @Override
                public long getMsExpectedWaitingTime() {
                    return 2000;
                }
            };

            TestWaitHelper twh = new TestWaitHelper(callback, threadFactory, testAccess);
            twh.waitUntilDone(5 * 60 * 1000); // use a very long timeout to allow all tests to finish
        } catch ( InterruptedException x ) {
            // this should not happen, but it's a failure if it does
            failureExceptionWithStackTrace(x);
        } finally {
            outln("*****************************************");
        }
    }

    private void testWaitingAlreadyMet() {
        try {
            outln("***** Testing: waitUntilFull - when already full before calling *****");

            TestWaitHelper.Callback callback = new AbstractCallback() {
                @Override
                public TestWaitHelper.Callback.Control createNew(Object lockObject) {
                    fifo = createDS(10, lockObject);
                    addBulk(fifo, getFruits(10));

                    outln("count=" + fifo.getCount() + ", isEmpty=" + fifo.isEmpty() + ", isFull=" + fifo.isFull());
                    return TestWaitHelper.Callback.Control.DO_NOTHING;
                }

                @Override
                public long getMsExpectedWaitingTime() {
                    return 0;
                }
            };

            TestWaitHelper twh = new TestWaitHelper(callback, threadFactory, testAccess);
            twh.waitUntilDone(5 * 60 * 1000); // use a very long timeout to allow all tests to finish
        } catch ( InterruptedException x ) {
            // this should not happen, but it's a failure if it does
            failureExceptionWithStackTrace(x);
        } finally {
            outln("*****************************************");
        }
    }

    private static abstract class AbstractCallback implements TestWaitHelper.Callback {
        protected PPBoundedFifo<String> fifo; // set by createNew()

        protected AbstractCallback() {
        }

        /** Must set fifo field */
        @Override
        public abstract Control createNew(Object lockObject);

        @Override
        public abstract long getMsExpectedWaitingTime();

        @Override
        public boolean shouldDoWaitWithTimeout() {
            return true;
        }

        @Override
        public String getDisplayNameForWaitWithTimeout() {
            return "waitUntilFull";
        }

        @Override
        public boolean callWaitWithTimeout(long msTimeout) throws InterruptedException {
            return fifo.waitUntilFull(msTimeout);
        }

        @Override
        public boolean shouldDoWaitNoTimeout() {
            return true;
        }

        @Override
        public String getDisplayNameForWaitNoTimeout() {
            return "waitUntilFull";
        }

        @Override
        public void callWaitNoTimeout() throws InterruptedException {
            fifo.waitUntilFull();
        }
    } // type AbstractCallback
}
