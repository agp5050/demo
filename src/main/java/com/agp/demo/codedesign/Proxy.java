package com.agp.demo.codedesign;

public class Proxy {
    public static void main(String[] args) {
        Car car=new Car();
        /*
before move...
car moving ...
spent 0 milliseconds
after move ...
         */
        //Amazing ...代理实现。优于集成。
        new LogMovableProxy(new TimeMovableProxy(car)).move();
    }
}

interface Movable{
    void move();
}

class Car implements Movable{
    @Override
    public void move() {
        System.out.println("car moving ...");
    }
}

class LogMovableProxy implements Movable{
    private Movable movable;

    public LogMovableProxy(Movable movable) {
        this.movable = movable;
    }

    @Override
    public void move() {
        // Do some Proxy things.
        System.out.println("before move...");
        movable.move();
        System.out.println("after move ...");
    }
}

class TimeMovableProxy implements Movable{
    private Movable movable;

    public TimeMovableProxy(Movable movable) {
        this.movable = movable;
    }

    @Override
    public void move() {
        long startT=System.currentTimeMillis();
        movable.move();
        long endT=System.currentTimeMillis();
        System.out.println("spent "+(endT-startT)+" milliseconds");
    }
}