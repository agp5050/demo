package com.agp.demo.spi;

public class PythonDeveloper implements Developer{
    @Override
    public void sayHi() {
        System.out.println("I'm python developer.");
    }
}
