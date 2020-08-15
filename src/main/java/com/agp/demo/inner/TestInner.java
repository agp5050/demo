package com.agp.demo.inner;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Slf4j
public class TestInner {
    @Test
    public void test(){
        int a=20;
        int a1=20;
        int b=--a;
        int b1=a--;
       log.info("b:{}; b1:{}",b,b1);
    }
    @Test
    public void testSetIterator(){
        Set<String> strings=new HashSet<>();
        strings.add("aa");
        strings.add("aa1");
        strings.add("aa2");
        strings.add("aa3");
        Iterator<String> iterator = strings.iterator();
        while (iterator.hasNext()){
            String next = iterator.next();
            System.out.println(next);
        }
    }


}
