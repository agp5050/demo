package com.agp.demo.cglib;

import com.agp.demo.Animal;
import com.agp.demo.Dog;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JDKProxyDemo {
    public static void main(String[] args) {
        Animal dog=new Dog();
        InvocationHandler invocationHandler=new CustomInvocationHandler(dog);
        /*
        *此处不能强转为Dog。 因为被代理的是接口。
        */
        Animal dogProxy = (Animal)Proxy.newProxyInstance(JDKProxyDemo.class.getClassLoader(), Dog.class.getInterfaces(), invocationHandler);
        dogProxy.eat();
        dogProxy.say("Hello world.");

    }
}
class CustomInvocationHandler implements InvocationHandler{
    Animal animal;
    public CustomInvocationHandler(Animal animal){
        this.animal=animal;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("JDK dynamic proxy... add some salt..");
        Object invoke = method.invoke(animal,args);
        System.out.println("JDK dynamic proxy... add some salt.. and cook");
        return invoke;
    }
}