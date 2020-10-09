package com.agp.demo.inner;

import org.junit.Test;

public class Outer {
    private String name="outer...";
    @Test
    public  void main() {
        new Outer.Inner().testInner();
    }
    private void privateMtd(){
        System.out.println("privateMtd");
    }
    public void publicMtd(){
        System.out.println("publicMtd");
    }

    /**
     * Inner class可以随意调用outer class的方法和属性。
     * Inner class初始化，必然要初始化outer class
     */
    private class Inner{
        public void testInner(){
            System.out.println("testInner before");
            publicMtd();
            privateMtd();
            System.out.println(name);
            System.out.println("testInner after");

        }

    }
}
