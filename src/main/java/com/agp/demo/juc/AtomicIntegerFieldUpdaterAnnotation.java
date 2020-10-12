package com.agp.demo.juc;

public class AtomicIntegerFieldUpdaterAnnotation {
    /*AtomicIntegerFieldUpdater<T> 可以对泛型T类型的指定name的field进行原子更新
     * 根据反射,获取field，每个field在instance里面都有固定的偏移量。 然后
     * U.compareAndSwapInt(obj, offset, expect, update) 根据特定的instance，和偏移量确定要更新的
     * int field的地址。 */
    /**AtomicIntegerFieldUpdater<T> 各个线程必须使用同一个updater，因为必须确保都用同一个的
     * unsafe对象进行cas 多个unsafe肯定不行的( field 必须为volatile 而且必须为 int类型 不能 Integer)*/



}

class Person {
    //这种field才可以使用FieldUpdater
    public volatile int id;
    private String name;
    private Integer age;

    public Person() {
    }

    public Person(Integer id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

}