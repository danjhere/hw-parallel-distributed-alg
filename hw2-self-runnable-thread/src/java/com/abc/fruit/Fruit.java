package com.abc.fruit;

/**
 *  Fruit is a self-running object.  (It starts up an internal thread during construction).
 *
 * The internal thread for a Fruit instance will print the name supplied at construction 20 times.
 * Optionally, the internal thread may sleep for a short period of time between printing lines.
 */
public class Fruit {

    private Thread internalThread;

    private volatile boolean noStopRequested;

    private String name;

    private int linesToPrint = 20;

    public Fruit(String name) {

        this.name = name;
        this.noStopRequested = true;

        internalThread = new Thread( new Runnable() {
            @Override
            public void run() {
                doWork();
            }
        }, name);

        internalThread.start();
    }

    private void doWork() {

        int i=0;
        while (noStopRequested && i<linesToPrint){
            System.out.println(name);
            try {
                internalThread.sleep(5);
            } catch ( InterruptedException e ) {
                System.err.println("InterruptedException " + e.getMessage());
            }
            i++;
        }
    }

    public void stopRequest() {
        noStopRequested = false;
        internalThread.interrupt();
    }

    public boolean isAlive() {
        return internalThread.isAlive();
    }
}
