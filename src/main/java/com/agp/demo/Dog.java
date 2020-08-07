package com.agp.demo;

public class Dog implements Animal {
    @Override
    public String say(String msg) {
        System.out.println("doggy say "+msg);
        return "doggy say "+msg;
    }

    @Override
    public String eat() {
        System.out.println("Doggy eat's bones...");
        return "Doggy eat's bones...";
    }
}
