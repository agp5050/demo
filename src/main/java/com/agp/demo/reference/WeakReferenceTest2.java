package com.agp.demo.reference;

import com.agp.demo.annotation.TestIntension;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.lang.ref.WeakReference;
@Slf4j
public class WeakReferenceTest2 {
    class A{};
    class B{
        WeakReference<A> weakReference;
        public B(A a){
           weakReference=new WeakReference<A>(a);
        }
        public A getA(){
          return   weakReference.get();
        }
    }
    @TestIntension("一个对象被多个对象引用时，将其中一个引用变量置为0，其他对象还继续指向，不回收。通过weakReference就OK了")
    @Test
    public void testWeakReference(){
        A a=new A();
        B b=new B(a);
        a=null;
        System.gc();
        log.info("b.getA():{}",b.getA()); //b.getA():null
    }
}
