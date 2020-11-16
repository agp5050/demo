package com.agp.demo.juc;

import com.agp.demo.Person;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class JucTest {
    public static void main(String[] args) throws NoSuchFieldException {
        ConcurrentHashMap concurrentHashMap;
        System.out.println(Person.class.getDeclaredField("id").getType());
        System.out.println(Person.class.getDeclaredField("id").getModifiers());
    }
    @Test
    public void testAtomicIntegerFieldUpdater(){
        Thread a;
        AtomicIntegerFieldUpdater<Person> atomicIntegerFieldUpdater = AtomicIntegerFieldUpdater.newUpdater(Person.class, "id");
        Person person = new Person();
        atomicIntegerFieldUpdater.getAndIncrement(person);
        System.out.println(person.getId());
    }
}
