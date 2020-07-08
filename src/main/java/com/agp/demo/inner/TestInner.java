package com.agp.demo.inner;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
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


}
