package com.agp.demo.utils;

import com.agp.demo.annotation.RightInvoke;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;
import org.springframework.beans.factory.InitializingBean;

import java.util.Calendar;
import java.util.Map;

public class TodayOddEvenFunction extends AbstractFunction  implements InitializingBean {

    @Override
    public AviatorObject call(Map<String, Object> env) {
        Calendar ca = Calendar.getInstance();
        return new AviatorRuntimeJavaType(ca.get(Calendar.DAY_OF_YEAR)%2);
    }

    @Override
    public String getName() {
        return "today_odd_even";
    }
    @RightInvoke("InitializingBean 初始化后，自动注册这个Aviator。 这个在spring自动管理的时候很有用")
    @Override
    public void afterPropertiesSet() throws Exception {
        AviatorEvaluator.addFunction(this);
    }


}
