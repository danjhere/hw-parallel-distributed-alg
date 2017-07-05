package com.abc.pp.producerconsumer;

import com.abc.pp.producerconsumer.Pond.PondState;

public class Alice {
    private final Pond pond;

    public Alice(Pond pond) {
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
                ThreadTools.outln("waiting until FOOD_ONLY in Pond...");
                pond.waitUntilStateIs(PondState.FOOD_ONLY);

                ThreadTools.outln("I see that there's food in the pond, releasing pets");

                // release the pets
                pond.setPondState(PondState.PETS);

                // wait for pets to finish eating
                Thread.sleep(1000);

                ThreadTools.outln("pets are done eating, they're back inside, marking pond as EMPTY");

                // todo - when pets are done eating all the food
                pond.setPondState(PondState.EMPTY);
            }
        } catch ( InterruptedException x ) {
            // ignore
        } finally {
            System.out.println(Thread.currentThread().getName() + " finished");
        }
    }
}
