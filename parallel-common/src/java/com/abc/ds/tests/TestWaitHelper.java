package com.abc.ds.tests;

import com.programix.testing.*;
import com.programix.thread.*;

/**
 * Test helper which calls waitWhileXYZ and waitUntilXYZ methods and
 * exercises them with various timeouts, etc.
 */
public class TestWaitHelper {
    private final TestDSHelper.TestAccess testAccess;
    private final TestThreadFactory threadFactory;
    private final TestDSBase.RunState runState;

    private final Callback callback;
    private final Object lockObject;

    public TestWaitHelper(Callback callback,
                          TestThreadFactory threadFactory,
                          TestDSHelper.TestAccess testAccess) {

        this.callback = callback;
        this.threadFactory = threadFactory;
        this.testAccess = testAccess;

        lockObject = new Object();

        runState = new TestDSBase.RunState();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                runWork();
            }
        };

        threadFactory.createThreadFor(r, "WaitHelper");
    }

    private void runWork() {
        runState.registerCallerAsInternalThread();
        try {
            testAccess.outln("-----------------------");
            doTests(false, false);
            testAccess.outln("-----------------------");
            doTests(true, false);
            testAccess.outln("-----------------------");
            doTests(false, true);
            testAccess.outln("-----------------------");
//            doTests(true, true);
//            testAccess.outln("-----------------------");
        } catch ( InterruptException x ) {
            // ignore and die
        } catch ( InterruptedException x ) {
            // ignore and die
        } finally {
            runState.setNoLongerRunning();
        }
    }

    private void doTests(boolean useWackyWaiter,
                         boolean useNastyNotifier) throws InterruptedException {

        testAccess.outln("----- using WackyWaiter: " + (useWackyWaiter ? "yes" : "no") +
            ", using NastyNotifier: " + (useWackyWaiter ? "yes" : "no") + " -----");

        TestWackyWaiter wackyWaiter = null;
        TestNastyNotifier nastyNotifier = null;
        try {
            wackyWaiter = useWackyWaiter ? kickoffWackyWaiter() : null;
            nastyNotifier = useNastyNotifier ? kickoffNastyNotifier() : null;

            Thread.sleep(200);

            testNoTimeout();
            testWithPlentyOfTime();
            testWithZeroTimeout();
            testWithNotEnoughTime();
            testWithNegativeTimeout();
        } catch ( InterruptException x ) {
            // ignore and die
        } finally {
            if (wackyWaiter != null) wackyWaiter.stopRequest();
            if (nastyNotifier != null) nastyNotifier.stopRequest();

            if (wackyWaiter != null) wackyWaiter.waitUntilDone(2000);
            if (nastyNotifier != null) nastyNotifier.waitUntilDone(2000);
            ThreadTools.nap(200);
        }
    }

    private void testNoTimeout() throws InterruptedException {
        testAccess.outln("=== Calling without a timeout ===");
        Callback.Control callbackControl = callback.createNew(lockObject);
        try {
            double secExpectedWaitingTime = callback.getMsExpectedWaitingTime() / 1000.0;
            double tolerance = Math.max(0.02, secExpectedWaitingTime * 0.10);
            String methodName = callback.getDisplayNameForWaitNoTimeout();

            testAccess.outln(String.format("Attempting to call %s()...", methodName));
            NanoTimer timer = NanoTimer.createStarted();
            callback.callWaitNoTimeout();
            timer.stop();
            testAccess.outln("...finished, seconds of wait time: ", timer.getElapsedSeconds(), secExpectedWaitingTime, tolerance, 5);
        } finally {
            callbackControl.stopRequest();
            callbackControl.waitUntilDone(2000);
        }
    }

    private void testWithPlentyOfTime() throws InterruptedException {
        testAccess.outln("=== Calling with a timeout which should be plenty ===");
        Callback.Control callbackControl = callback.createNew(lockObject);
        try {
            boolean expectTimeout = false;
            double secExpectedWaitingTime = callback.getMsExpectedWaitingTime() / 1000.0;
            double tolerance = Math.max(0.02, secExpectedWaitingTime * 0.10);
            String methodName = callback.getDisplayNameForWaitWithTimeout();

            long msTimeout = Math.round(2 * secExpectedWaitingTime * 1000);

            testAccess.outln(String.format("Attempting to call %s(%d)...", methodName, msTimeout));
            NanoTimer timer = NanoTimer.createStarted();
            boolean timedOut = !callback.callWaitWithTimeout(msTimeout);
            timer.stop();
            testAccess.outln("timed out", timedOut, expectTimeout);
            testAccess.outln("...finished, seconds of wait time: ", timer.getElapsedSeconds(), secExpectedWaitingTime, tolerance, 5);
        } finally {
            callbackControl.stopRequest();
            callbackControl.waitUntilDone(2000);
        }
    }

    private void testWithNotEnoughTime() throws InterruptedException {
        testAccess.outln("=== Calling with a timeout which should be too short ===");
        testAccess.outln("===     if condition is met before calling, then we do NOT expect a timeout");
        testAccess.outln("===     if condition is NOT met before calling, then we DO expect a timeout");
        Callback.Control callbackControl = callback.createNew(lockObject);
        try {
            boolean expectTimeout = callback.getMsExpectedWaitingTime() > 0;
            double secExpectedWaitingTime = callback.getMsExpectedWaitingTime() / 1000.0;
            double tolerance = Math.max(0.02, secExpectedWaitingTime * 0.10);
            String methodName = callback.getDisplayNameForWaitWithTimeout();

            long msTimeout = Math.round(0.5 * secExpectedWaitingTime * 1000);

            testAccess.outln(String.format("Attempting to call %s(%d)...", methodName, msTimeout));
            NanoTimer timer = NanoTimer.createStarted();
            boolean timedOut = !callback.callWaitWithTimeout(msTimeout);
            timer.stop();
            testAccess.outln("timed out", timedOut, expectTimeout);
            testAccess.outln("...finished, seconds of wait time: ", timer.getElapsedSeconds(), msTimeout / 1000.0, tolerance, 5);
        } finally {
            callbackControl.stopRequest();
            callbackControl.waitUntilDone(2000);
        }
    }

    private void testWithNegativeTimeout() throws InterruptedException {
        testAccess.outln("=== Calling with a NEGATIVE timeout ===");
        testAccess.outln("===     if condition is met before calling, then we do NOT expect a timeout");
        testAccess.outln("===     if condition is NOT met before calling, then we DO expect a timeout");
        Callback.Control callbackControl = callback.createNew(lockObject);
        try {
            boolean expectTimeout = callback.getMsExpectedWaitingTime() > 0;
            double secExpectedWaitingTime = callback.getMsExpectedWaitingTime() / 1000.0;
            double tolerance = Math.max(0.02, secExpectedWaitingTime * 0.10);
            String methodName = callback.getDisplayNameForWaitWithTimeout();

            long msTimeout = -10;

            testAccess.outln(String.format("Attempting to call %s(%d)...", methodName, msTimeout));
            NanoTimer timer = NanoTimer.createStarted();
            boolean timedOut = !callback.callWaitWithTimeout(msTimeout);
            timer.stop();
            testAccess.outln("timed out", timedOut, expectTimeout);
            testAccess.outln("...finished, seconds of wait time: ", timer.getElapsedSeconds(), 0.0, tolerance, 5);
        } finally {
            callbackControl.stopRequest();
            callbackControl.waitUntilDone(2000);
        }
    }

    private void testWithZeroTimeout() throws InterruptedException {
        testAccess.outln("=== Calling with a timeout of 0 (wait without ever timing out) ===");
        Callback.Control callbackControl = callback.createNew(lockObject);
        try {
            double secExpectedWaitingTime = callback.getMsExpectedWaitingTime() / 1000.0;
            double tolerance = Math.max(0.02, secExpectedWaitingTime * 0.10);
            String methodName = callback.getDisplayNameForWaitWithTimeout();

            long msTimeout = 0;

            testAccess.outln(String.format("Attempting to call %s(%d)...", methodName, msTimeout));
            NanoTimer timer = NanoTimer.createStarted();
            callback.callWaitWithTimeout(msTimeout);
            timer.stop();
            testAccess.outln("...finished, seconds of wait time: ", timer.getElapsedSeconds(), secExpectedWaitingTime, tolerance, 5);
        } finally {
            callbackControl.stopRequest();
            callbackControl.waitUntilDone(2000);
        }
    }

    public void stopRequest() {
        runState.stopRequest();
    }

    public boolean waitUntilDone(long msTimeout) throws InterruptedException {
        return runState.waitWhileStillRunning(msTimeout);
    }

    private TestWackyWaiter kickoffWackyWaiter(){
        return new TestWackyWaiter(lockObject, threadFactory, testAccess);
    }

    private TestNastyNotifier kickoffNastyNotifier() {
        return new TestNastyNotifier(lockObject, threadFactory, testAccess);
    }

    public static interface Callback {
        Callback.Control createNew(Object lockObject);
        long getMsExpectedWaitingTime(); // zero mean the condition has already been met: no waiting

        boolean shouldDoWaitWithTimeout();
        String getDisplayNameForWaitWithTimeout();
        boolean callWaitWithTimeout(long msTimeout) throws InterruptedException;

        boolean shouldDoWaitNoTimeout();
        String getDisplayNameForWaitNoTimeout();
        void callWaitNoTimeout() throws InterruptedException;

        public static interface Control {
            public static final Control DO_NOTHING = new Control() {
                @Override
                public void stopRequest() {
                    // do nothing
                }

                @Override
                public boolean waitUntilDone(long msTimeout) throws InterruptedException {
                    return true; // nothing to wait for
                }
            };

            void stopRequest();
            boolean waitUntilDone(long msTimeout) throws InterruptedException;
        } // type Callback.Control
    } // type Callback
}
