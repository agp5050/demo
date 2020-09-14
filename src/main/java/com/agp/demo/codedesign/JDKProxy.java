package com.agp.demo.codedesign;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JDKProxy {
    public static void main(String[] args) {
        Car car=new Car();

        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles","true");
        Movable o = (Movable)Proxy.newProxyInstance(Car.class.getClassLoader(), Car.class.getInterfaces(), new LogInvocationHandler(car));
        o.move();
    }
}
class LogInvocationHandler implements InvocationHandler{
    Car car;
    public LogInvocationHandler(Car car){
        this.car=car;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("JDK log proxy before car...");
        return method.invoke(car,args);
    }
}