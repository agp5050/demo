package com.agp.demo.aviator;

import com.agp.demo.utils.TodayOddEvenFunction;
import com.googlecode.aviator.AviatorEvaluator;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Test
    public void testStringAvaitor(){
        String str="customerIds != nil && count(customerIds ) > 0";
        List<Integer> customerIds = Arrays.asList(1, 2, 3);
        Map<String,Object> map=new HashMap<>();
        map.put("customerIds",customerIds);
        Object execute = AviatorEvaluator.execute(str, map);
        System.out.println(execute);
    }
}
