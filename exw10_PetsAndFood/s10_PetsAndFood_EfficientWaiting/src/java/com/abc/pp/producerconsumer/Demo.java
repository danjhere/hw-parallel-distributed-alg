package com.abc.pp.producerconsumer;

public class Demo {
    public static void main(String[] args) {
        System.out.println("Efficient waiting....");
        Pond pond = new Pond();
        new Alice(pond);
        new Bob(pond);
    }
}
