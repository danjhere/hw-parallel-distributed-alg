package com.abc.pp.fifo.tests;

import com.abc.pp.fifo.*;
import com.abc.pp.fifo.impl.*;
import com.abc.pp.fifo.tests.gui.*;

public class TestCircularArrayBoundedFifo {
    public static void main(String[] args) {
        GuiTestPPBoundedFifo.runTests(
            "Testing of CircularArrayPPBoundedFifo",
            new PPBoundedFifoFactory() {
                @Override
                public <T> PPBoundedFifo<T> create(Class<T> itemType, int capacity) {
                    return new CircularArrayPPBoundedFifo<T>(capacity, itemType);
                }

                @Override
                public <T> PPBoundedFifo<T> create(Class<T> itemType,
                                                   int capacity,
                                                   Object lockObject) {

                    return new CircularArrayPPBoundedFifo<T>(capacity, itemType, lockObject);
                }
            });
    }
}