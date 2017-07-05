package com.abc.pp.producerconsumer;

import com.abc.pp.producerconsumer.Pond.PondState;

public class Bob {
    private final Pond pond;

    public Bob(Pond pond) {
        this.pond = pond;

        Runnable r = new Runnable() {
            @Override
            public void run() {
                runWork();
            }
        };

        Thread t = new Thread(r, getClass().getSimpleName());
        t.start();
    }

    private void runWork() {
        try {
            while (true) {
                ThreadTools.outln("waiting until pond is empty...");
                while (pond.getPondState() != PondState.EMPTY);

                ThreadTools.outln("I see now that the pond is empty, putting food into it");
                // put food into pond
                Thread.sleep(500);

                ThreadTools.outln("food in in pond, setting pond to FOOD_ONLY");
                pond.setPondState(PondState.FOOD_ONLY);
            }
        } catch ( InterruptedException x ) {
            // ignore
        } finally {
            System.out.println(Thread.currentThread().getName() + " finished");
        }
    }
}
