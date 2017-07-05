package com.abc.pp.producerconsumer;

public class Pond {
    private PondState state;

    public Pond() {
        state = PondState.EMPTY;
    }

    public synchronized PondState getPondState() {
        return state;
    }

    public synchronized void setPondState(PondState proposedState)
            throws IllegalStateException {

        if (!state.canTransitionTo(proposedState)) {
            throw new IllegalStateException("in state=" + state +
                ", can't transition to " + proposedState);
        }

        state = proposedState;
        notifyAll();
    }

    public synchronized boolean waitUntilStateIs(PondState targetState,
                                                 long msTimeout)
            throws InterruptedException {

        if (state == targetState) {
            return ThreadTools.SUCCESS;
        }

        if (msTimeout == 0) {
            do {
                wait();
            } while (state != targetState);
            return ThreadTools.SUCCESS;
        }

        long msEndTime = System.currentTimeMillis() + msTimeout;
        long msRemaining = msTimeout;

        while (msRemaining > 0) {
            wait(msRemaining);
            if (state == targetState) {
                return ThreadTools.SUCCESS;
            }
            msRemaining = msEndTime - System.currentTimeMillis();
        }
        return ThreadTools.TIMED_OUT;
    }

    public synchronized void waitUntilStateIs(PondState targetState)
            throws InterruptedException {
        waitUntilStateIs(targetState, 0);
    }

    public static enum PondState {
        EMPTY,
        FOOD_ONLY,
        PETS;

        private PondState() {
        }

        public boolean canTransitionTo(PondState proposedNextState) {
            if (this == EMPTY) {
                return proposedNextState == FOOD_ONLY;
            }
            if (this == FOOD_ONLY) {
                return proposedNextState == PETS;
            }
            if (this == PETS) {
                return proposedNextState == EMPTY;
            }
            return false;
        }
    } // type PondState
}
