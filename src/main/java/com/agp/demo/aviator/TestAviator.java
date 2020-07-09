package com.agp.demo.aviator;

import com.agp.demo.utils.TodayOddEvenFunction;
import com.googlecode.aviator.AviatorEvaluator;
import org.junit.Test;

public class TestAviator {
    @Test
 public void test(){
     String script="str(123)";
     Object execute = AviatorEvaluator.execute(script);
     System.out.println(execute);
 }

    @Test
    public void testRegisterFunAndInvoke() throws Exception {
        String script="today_odd_even()";
        TodayOddEvenFunction function=new TodayOddEvenFunction();
        function.afterPropertiesSet();
        Object execute = AviatorEvaluator.execute(script);
        System.out.println(execute);

    }
}
