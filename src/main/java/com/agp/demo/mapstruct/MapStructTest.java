package com.agp.demo.mapstruct;

import com.agp.demo.Person;

/**
 * mapstruct 执行属性copy时耗时：
 * 分别执行1000、10000、100000、1000000次映射的耗时分别为：0ms、1ms、3ms、6ms
 */
public class MapStructTest {
    public static void main(String[] args) {
        org.slf4j.impl.StaticLoggerBinder a;
        Person person=new Person(1,"xxx",123);
        PersonVO personVO = PersonVOConverter.INSTANCE.getPersonVO(person);
        System.out.println(personVO);
    }
}
