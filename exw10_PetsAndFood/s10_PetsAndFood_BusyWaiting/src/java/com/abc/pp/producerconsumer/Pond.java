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
