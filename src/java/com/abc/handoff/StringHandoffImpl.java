package com.abc.handoff;

import com.abc.pp.stringhandoff.*;
import com.programix.thread.*;

/**
 * StringHandoff is used to pass a String from one thread to another.
 * The passer and the receiver meet inside an instance for the handoff.
 * For example, if pass() is called and there is not receiver waiting, the
 * thread calling pass() will block until the receiver arrives. Similarly,
 * if receive() is called and there is no passer waiting, the thread calling
 * receive() will block until the passer arrives.
 * <p>
 * There can only be one thread waiting to pass at any given time. If a second
 * thread tries to call pass() when another thread is already waiting to pass,
 * an IllegalStateException is thrown. Similarly, there can only be one thread
 * waiting to receive at any given time. If a second thread tries to call
 * receive() when another thread is already waiting to receive, an
 * IllegalStateException is thrown. IllegalStateException is a RuntimeException.
 * <p>
 * Methods that take a timeout parameter will throw a TimedOutException if
 * the specified number of milliseconds passes without the handoff occurring.
 * TimedOutException is a RuntimeException.
 * <p>
 * The methods that declare that they may throw a ShutdownException will do
 * so after shutdown() has been called. If a thread is waiting inside a method
 * and another thread calls shutdown(), then the waiting thread will throw
 * a ShutdownException. If a thread calls a method that declares that it may
 * throw a ShutdownException, and any thread has already called shutdown(), then
 * the call will immediately throw the ShutdownException.
 * ShutdownException is a RuntimeException.
 */
public class StringHandoffImpl implements StringHandoff {

    private String message;
    private boolean shutdown;
    private boolean waiting;


    public StringHandoffImpl() {}


    private String getMessage() {
        String temp = this.message;
        clearMessage();
        return temp;
    }

    private void clearMessage(){
        this.message = null;
        getLockObject().notifyAll();
    }

    private void setMessage(String msg) {
        if (this.message == null) {
            this.message = msg;
            getLockObject().notifyAll();
        } else {
            throw new IllegalStateException();
        }
    }

    private void setWaiting() {
        if (this.waiting) {
            throw new IllegalStateException();
        } else {
            this.waiting = true;
        }
    }

    private void clearWaiting(){
        this.waiting = false;
    }

    private void checkForShutdown(){
        if (shutdown) {
            throw new ShutdownException();
        }
    }

    @Override
    public synchronized void pass(String msg, long msTimeout)
            throws InterruptedException, TimedOutException, ShutdownException, IllegalStateException {

        checkForShutdown();
        setMessage(msg);

        try {

            if (msTimeout <= 0) {
                while (message != null) {
                    checkForShutdown();
                    getLockObject().wait();
                }
                return;
            }

            long endTime = System.currentTimeMillis() + msTimeout;
            long msRemaining = msTimeout;

            while (msRemaining > 0) {
                checkForShutdown();
                if (message == null) {
                    return;
                }
                getLockObject().wait(msRemaining);
                msRemaining = endTime - System.currentTimeMillis();
            }
            throw new TimedOutException();
        } finally {
            clearMessage();
        }
    }

    @Override
    public synchronized void pass(String msg)
            throws InterruptedException, ShutdownException, IllegalStateException {
        pass(msg, 0);
    }


    @Override
    public synchronized String receive(long msTimeout)
            throws InterruptedException, TimedOutException, ShutdownException, IllegalStateException {

        checkForShutdown();
        setWaiting();

        try {

            if (msTimeout <= 0) {
                while (message == null) {
                    getLockObject().wait();
                    checkForShutdown();
                }
                return getMessage();
            }

            long endTime = System.currentTimeMillis() + msTimeout;
            long msRemaining = msTimeout;
            while (msRemaining > 0) {
                if (message != null) {
                    return getMessage();
                }
                getLockObject().wait(msRemaining);
                msRemaining = endTime - System.currentTimeMillis();
                checkForShutdown();
            }
            throw new TimedOutException();
        }
        finally {
            clearWaiting();
        }
    }

    @Override
    public synchronized String receive()
            throws InterruptedException, ShutdownException, IllegalStateException {
        return receive(0);
    }

    @Override
    public synchronized void shutdown() {
        this.shutdown = true;
        clearMessage();
        clearWaiting();
        notifyAll();
    }

    @Override
    public Object getLockObject() {
        return this;
    }
}