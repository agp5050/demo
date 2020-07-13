package com.agp.demo.spi;

public class JavaDeveloper implements Developer {
    @Override
    public void sayHi() {
        System.out.println("I'm java developer.");
    }
}
