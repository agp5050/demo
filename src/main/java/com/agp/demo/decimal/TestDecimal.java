package com.agp.demo.decimal;

import com.agp.demo.annotation.Result;
import com.agp.demo.annotation.RightInvoke;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Slf4j
public class TestDecimal {
    @RightInvoke("float 4个字节占用空间少，BigDecimal占用空间多，计算慢")
    @Test
    public void testDecimalFloat(){
        float a=0.30f;
        float b=0.60f;
        log.info("float a+b={}",a+b);   //0.90000004
        //flow 4个字节
        BigDecimal bigDecimal=new BigDecimal(a,new MathContext(2, RoundingMode.HALF_UP));
        BigDecimal bigDecimalB=new BigDecimal(b,new MathContext(2, RoundingMode.HALF_UP));
        log.info("bigDecimal.add(bigDecimalB).doubleValue():{}",bigDecimal.add(bigDecimalB).toString());  //0.90

    }
}
