package com.abc.pp.fifo.tests;

import com.abc.ds.tests.*;
import com.abc.pp.fifo.*;
import com.programix.testing.*;

/* deliberate package access */
class TestPPBoundedFifoWaitUntilEmpty extends TestPPBoundedFifoBase {
    public TestPPBoundedFifoWaitUntilEmpty(PPBoundedFifoFactory factory,
                                           TestThreadFactory threadFactory) {
        super("waitUntilEmpty", factory, threadFactory);
    }

    @Override
    protected void performTests() {
        testWaitingToBeMet();
        testWaitingAlreadyMet();
    }

    private void testWaitingToBeMet() {
        try {
            outln("***** Testing: waitUntilEmpty - when not initially empty *****");

            TestWaitHelper.Callback callback = new AbstractCallback() {
                @Override
                public TestWaitHelper.Callback.Control createNew(Object lockObject) {
                    fifo = createDS(10, lockObject);

                    String[] fruits = getFruits(3);
                    addBulk(fifo, fruits);
                    outln("count=" + fifo.getCount() + ", isEmpty=" + fifo.isEmpty() + ", isFull=" + fifo.isFull());

                    final Remover<String> remover = new Remover<>(fifo, 0, 1000);

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
                    return 2000;
                }
            };

            TestWaitHelper testWaitHelper = new TestWaitHelper(callback, threadFactory, testAccess);
            testWaitHelper.waitUntilDone(5 * 60 * 1000); // use a very long timeout to allow all tests to finish
        } catch ( InterruptedException x ) {
            // this should not happen, but it's a failure if it does
            failureExceptionWithStackTrace(x);
        } finally {
            outln("*****************************************");
        }
    }

    private void testWaitingAlreadyMet() {
        try {
            outln("***** Testing: waitUntilEmpty - when already empty before calling *****");

            TestWaitHelper.Callback callback = new AbstractCallback() {
                @Override
                public TestWaitHelper.Callback.Control createNew(Object lockObject) {
                    fifo = createDS(10, lockObject);
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
            return "waitUntilEmpty";
        }

        @Override
        public boolean callWaitWithTimeout(long msTimeout) throws InterruptedException {
            return fifo.waitUntilEmpty(msTimeout);
        }

        @Override
        public boolean shouldDoWaitNoTimeout() {
            return true;
        }

        @Override
        public String getDisplayNameForWaitNoTimeout() {
            return "waitUntilEmpty";
        }

        @Override
        public void callWaitNoTimeout() throws InterruptedException {
            fifo.waitUntilEmpty();
        }
    } // type AbstractCallback
}
