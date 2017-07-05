package com.abc.pp.fifo.tests;

import com.abc.ds.tests.*;
import com.abc.pp.fifo.*;
import com.programix.testing.*;

/* deliberate package access */
class TestPPBoundedFifoWaitWhileFull extends TestPPBoundedFifoBase {
    public TestPPBoundedFifoWaitWhileFull(PPBoundedFifoFactory factory,
                                          TestThreadFactory threadFactory) {
        super("waitWhileFull", factory, threadFactory);
    }

    @Override
    protected void performTests() {
        testWaitingToBeMet();
        testWaitingAlreadyMet();
    }

    private void testWaitingToBeMet() {
        try {
            outln("***** Testing: waitWhileFull - when initially full *****");

            TestWaitHelper.Callback callback = new AbstractCallback() {
                @Override
                public TestWaitHelper.Callback.Control createNew(Object lockObject) {
                    fifo = createDS(10, lockObject);

                    String[] fruits = getFruits(10);
                    addBulk(fifo, fruits);
                    outln("count=" + fifo.getCount() + ", isEmpty=" + fifo.isEmpty() + ", isFull=" + fifo.isFull());

                    final Remover<String> remover = new Remover<>(fifo, 1000, 1000);

                    return new TestWaitHelper.Callback.Control() {
                        @Override
                        public void stopRequest() {
                            remover.stopRequest();
                        }

                        @Override
                        public boolean waitUntilDone(long msTimeout) throws InterruptedException {
                            return remover.waitUntilDone(msTimeout);
                        }
                    };
                }

                @Override
                public long getMsExpectedWaitingTime() {
                    return 1000;
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
            outln("***** Testing: waitWhileFull - when already not full before calling *****");

            TestWaitHelper.Callback callback = new AbstractCallback() {
                @Override
                public TestWaitHelper.Callback.Control createNew(Object lockObject) {
                    fifo = createDS(10, lockObject);
                    String[] fruits = getFruits(5);
                    addBulk(fifo, fruits);
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
            return "waitWhileFull";
        }

        @Override
        public boolean callWaitWithTimeout(long msTimeout) throws InterruptedException {
            return fifo.waitWhileFull(msTimeout);
        }

        @Override
        public boolean shouldDoWaitNoTimeout() {
            return true;
        }

        @Override
        public String getDisplayNameForWaitNoTimeout() {
            return "waitWhileFull";
        }

        @Override
        public void callWaitNoTimeout() throws InterruptedException {
            fifo.waitWhileFull();
        }
    } // type AbstractCallback
}
