package com.agp.demo.cglib;

import com.agp.demo.Dog;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibDemo {
    public static void main(String[] args) {
        Dog dog = (Dog)CglibClassHandler.getClassProxy(Dog.class);
        dog.say("Hello world.");
        dog.eat();

        Dog doggy=new Dog();

        Dog doggyProxy =(Dog) CglibObjectHandler.getObjectProxy(doggy);
        doggyProxy.eat();
        doggyProxy.say("It's a good day.");
        System.out.println(dog==doggyProxy);  // 对象不是同一个对象，一个是代理创建的一个是本身代码创建的。
        //后台代理好像一样。
    }
}

class CglibClassHandler implements MethodInterceptor{
    private Object target;
    public CglibClassHandler(){}
    public CglibClassHandler(Object target){
        this.target=target;
    }
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println(o.getClass().hashCode());  //可以调用o的类对象的方法。不能调用o的方法。这个O是代理对象。 会死循环。
        System.out.printf("Cglib proxy obj:%s, method:%s,proxy super name:%s",o.getClass().getName(),method.getName(),methodProxy.getSuperName());
        Object o1 = methodProxy.invokeSuper(o, objects);
        System.out.println("Cglib proxy cook.. finished.");
        return o1;
    }
    public static Object getClassProxy(Class cls){
        Enhancer enhancer=new Enhancer();
        enhancer.setSuperclass(cls);
        enhancer.setCallback(new CglibClassHandler());
        return enhancer.create();
    }

}

class CglibObjectHandler implements MethodInterceptor{
    private Object target;
    public CglibObjectHandler(){}
    public CglibObjectHandler(Object target){
        this.target=target;
    }
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println(o.getClass().hashCode()); //可以调用o的类对象的方法。不能调用o的方法。这个O是代理对象。 会死循环。
        System.out.printf("Cglib proxy obj:%s, method:%s,proxy super name:%s",o.getClass().getName(),method.getName(),methodProxy.getSuperName());
        Object o1 = methodProxy.invoke(target, objects);
        System.out.println("Cglib proxy cook.. finished.");
        return o1;
    }
    public static Object getObjectProxy(Object object){
        Enhancer enhancer=new Enhancer();
        enhancer.setSuperclass(object.getClass());
        enhancer.setCallback(new CglibObjectHandler(object));
        return enhancer.create();
    }


}

