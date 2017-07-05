package com.abc.pp.fifo.tests;

import java.util.*;

import com.abc.ds.tests.*;
import com.abc.pp.fifo.*;
import com.programix.testing.*;
import com.programix.thread.*;
import com.programix.thread.ix.*;
import com.programix.util.*;

/* deliberate package access */
abstract class TestPPBoundedFifoBase extends TestDSBase {
    protected final PPBoundedFifoFactory factory;
    protected final TestDSHelper<String> testHelper;
    protected final TestThreadFactory threadFactory;

    protected TestPPBoundedFifoBase(String titleSuffix,
                                    PPBoundedFifoFactory factory,
                                    TestThreadFactory threadFactory) {

        super("PPBoundedFifo - " + titleSuffix);
        this.factory = factory;
        this.threadFactory = threadFactory;
        testHelper = new TestDSHelper.Builder<String>()
            .setItemType(String.class)
            .setTestAccess(testAccess)
            .setAllowDuplicates(true)
            .setOrderMatters(true)
            .setWrapItemsInQuotes(true)
            .create();
    }

    protected TestPPBoundedFifoBase(String titleSuffix,
                                    PPBoundedFifoFactory factory) {
        this(titleSuffix, factory, null);
    }

    protected PPBoundedFifo<String> createDS() {
        int capacity = 100; // pretty big to for basic tests
        return createDS(capacity);
    }

    protected PPBoundedFifo<String> createDS(int capacity) {
        outln("Creating a new PPBoundedFifo<String> instance with capacity=" +
            capacity + " ...");
        PPBoundedFifo<String> fifo = factory.create(String.class, capacity);
        outln("   ...created: " + fifo.getClass().getCanonicalName());
        return fifo;
    }

    protected PPBoundedFifo<String> createDS(int capacity, Object lockObject) {
        outln("Creating a new PPBoundedFifo<String> instance with capacity=" +
            capacity + " and using a specific lockObject ...");
        PPBoundedFifo<String> fifo = factory.create(String.class, capacity, lockObject);
        outln("   ...created: " + fifo.getClass().getCanonicalName());

        boolean correctLockObject = fifo.getLockObject() == lockObject;
        if (correctLockObject) {
            outln("fifo.getLockObject() returned the requested lockObject", true);
        } else {
            outln("fifo.getLockObject() did NOT return the requested lockObject", false);
        }
        return fifo;
    }

    protected void add(PPBoundedFifo<String> fifo, String... items) {
        try {
            if (items == null) items = StringTools.ZERO_LEN_ARRAY;

            for ( String item : items ) {
                fifo.add(item);
                outln("add(" + StringTools.quoteWrap(item) + ")");
            }
        } catch ( InterruptedException x ) {
            failureExceptionWithStackTrace(x);
        }
    }

    protected void addBulk(PPBoundedFifo<String> fifo, String... items) {
        try {
            if (items == null) items = StringTools.ZERO_LEN_ARRAY;

            outln("Adding " + items.length + " items: " + formatCommaDelimited(items));
            for ( String item : items ) {
                fifo.add(item);
            }
        } catch ( InterruptedException x ) {
            failureExceptionWithStackTrace(x);
        }
    }

    protected void checkRemoveAll(PPBoundedFifo<String> ds,
                                  String... expectedItems) {

        testHelper.check("removeAll()", ds.removeAll(), expectedItems);
    }

    protected void checkRemoveAtLeastOne(PPBoundedFifo<String> ds,
                                         String... expectedItems) throws InterruptedException {

        testHelper.check("removeAtLeastOne()", ds.removeAtLeastOne(), expectedItems);
    }

    protected TestWackyWaiter kickoffWackyWaiter(Object lockObject) throws IllegalStateException {
        if (threadFactory == null) {
            throw new IllegalStateException("can't use WackyWaiter, no TestThreadFactory supplied at construction");
        }
        return new TestWackyWaiter(lockObject, threadFactory, testAccess);
    }

    protected TestNastyNotifier kickoffNastyNotifier(Object lockObject) throws IllegalStateException {
        if (threadFactory == null) {
            throw new IllegalStateException("can't use NastyNotifier, no TestThreadFactory supplied at construction");
        }
        return new TestNastyNotifier(lockObject, threadFactory, testAccess);
    }

    protected class Remover<T> {
        private final PPBoundedFifo<T> ds;
        private final long msInitialDelay;
        private final long msBetweenRemoveAttempts;
        private final ExpectedRemoveList expectedRemoveList;
        private final TestDSBase.RunState runState;

        public Remover(PPBoundedFifo<T> ds,
                       long msInitialDelay,
                       long msBetweenRemoveAttempts) {

            this.ds = ds;
            this.msInitialDelay = msInitialDelay;
            this.msBetweenRemoveAttempts = msBetweenRemoveAttempts;

            expectedRemoveList = new ExpectedRemoveList();

            runState = new TestDSBase.RunState();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    runWork();
                }
            };

            threadFactory.createThreadFor(r, "Remover");
        }

        private void runWork() {
            runState.registerCallerAsInternalThread();
            try {
                testAccess.outln("Remover starting");
                NanoTimer timer = NanoTimer.createStopped();
                if (msInitialDelay > 0) {
                    testAccess.outln("    Remover waiting for initial delay of " + msInitialDelay + "ms before attempting the first remove()");
                    Thread.sleep(msInitialDelay);
                }
                while ( runState.isKeepRunning() ) {
                    timer.resetAndStart();
                    T item = ds.remove();
                    timer.stop();

                    T expectedItem = expectedRemoveList.removeOrNull();
                    if ( expectedItem == null ) {
                        testAccess.outln(String.format(
                            "removed '%s' after blocking for %.5f seconds",
                            item, timer.getElapsedSeconds()));
                    } else {
                        String msg = String.format(
                            "removed item after blocking for %.5f seconds",
                            timer.getElapsedSeconds());
                        outln(msg, item, expectedItem);
                    }

                    Thread.sleep(msBetweenRemoveAttempts);
                }
            } catch ( InterruptedException x ) {
                // ignore and die
            } finally {
                testAccess.outln("Remover finished");
                runState.setNoLongerRunning();
            }
        }

        public void stopRequest() {
            runState.stopRequest();
        }

        public boolean waitUntilDone(long msTimeout) throws InterruptedException {
            return runState.waitWhileStillRunning(msTimeout);
        }

        public void appendExpectedItemToBeRemoved(T item) {
            expectedRemoveList.add(item);
        }

        public void appendExpectedItemsToBeRemoved(@SuppressWarnings("unchecked") T... items) {
            if (items == null || items.length == 0) return;
            for ( T item : items ) {
                appendExpectedItemToBeRemoved(item);
            }
        }

        public boolean waitUntilAllExpectedItemsHaveBeenRemoved(long msTimeout) throws InterruptedException {
            return expectedRemoveList.waitUntilEmpty(msTimeout);
        }

        private class ExpectedRemoveList {
            private final List<T> list;
            private final WaiterIx waiter;
            private final WaiterIx.Condition emptyCondition;


            public ExpectedRemoveList() {
                list = new LinkedList<>();
                waiter = new WaiterIx(this);
                emptyCondition = waiter.createCondition(new WaiterIx.Expression() {
                    @Override
                    public boolean isTrue() {
                        return list.isEmpty();
                    }
                });
            }

            public synchronized void add(T item) {
                list.add(item);
                waiter.signalChange();
            }

            public synchronized T removeOrNull() {
                if (list.isEmpty()) {
                    return null;
                }

                waiter.signalChange();
                return list.remove(0);
            }

            public synchronized boolean waitUntilEmpty(long msTimeout) throws InterruptedException {
                return emptyCondition.waitUntilTrue(msTimeout);
            }
        } // type ExpectedRemoveList
    } // type Remover

    protected class Adder<T> {
        private final PPBoundedFifo<T> ds;
        private final long msInitialDelay;
        private final long msBetweenAddAttempts;
        private final AddList addList;
        private final TestDSBase.RunState runState;

        public Adder(PPBoundedFifo<T> ds,
                     long msInitialDelay,
                     long msBetweenAddAttempts) {

            this.ds = ds;
            this.msInitialDelay = msInitialDelay;
            this.msBetweenAddAttempts = msBetweenAddAttempts;

            addList = new AddList();

            runState = new TestDSBase.RunState();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    runWork();
                }
            };

            threadFactory.createThreadFor(r, "Adder");
        }

        private void runWork() {
            runState.registerCallerAsInternalThread();
            try {
                testAccess.outln("Adder starting");
                NanoTimer timer = NanoTimer.createStopped();
                if (msInitialDelay > 0) {
                    testAccess.outln("    Adder waiting for initial delay of " + msInitialDelay + "ms before attempting the first add()");
                    Thread.sleep(msInitialDelay);
                }
                while ( runState.isKeepRunning() ) {
                    T itemToAdd = addList.remove(); // might block if they haven't supplied anything yet

                    timer.resetAndStart();
                    ds.add(itemToAdd);
                    timer.stop();

                    testAccess.outln(String.format(
                        "added '%s' after blocking for %.5f seconds",
                        itemToAdd, timer.getElapsedSeconds()));

                    Thread.sleep(msBetweenAddAttempts);
                }
            } catch ( InterruptedException x ) {
                // ignore and die
            } finally {
                testAccess.outln("Adder finished");
                runState.setNoLongerRunning();
            }
        }

        public void stopRequest() {
            runState.stopRequest();
        }

        public boolean waitUntilDone(long msTimeout) throws InterruptedException {
            return runState.waitWhileStillRunning(msTimeout);
        }

        public void appendItemToBeAdded(T item) {
            addList.add(item);
        }

        public void appendItemsToBeAdded(@SuppressWarnings("unchecked") T... items) {
            if (items == null || items.length == 0) return;
            for ( T item : items ) {
                appendItemToBeAdded(item);
            }
        }

        private class AddList {
            private List<T> list;

            public AddList() {
                list = new LinkedList<>();
            }

            public synchronized void add(T item) {
                list.add(item);
            }

            public synchronized T remove() throws InterruptedException {
                while (list.isEmpty()) {
                    wait();
                }
                return list.remove(0);
            }
        } // type Adder.AddList
    } // type Adder
}
