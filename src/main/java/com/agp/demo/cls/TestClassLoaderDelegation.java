package com.agp.demo.cls;

public class TestClassLoaderDelegation {
    public static void main(String[] args) {
        System.out.println(int.class.getClassLoader());  //null   (bootstrap class loader 非java类加载器)
        System.out.println(TestClassLoaderDelegation.class
                .getClassLoader().getClass().getCanonicalName()); //AppClassLoader
        System.out.println(TestClassLoaderDelegation.class
                .getClassLoader().getParent()); //ExtClassLoader
        System.out.println(TestClassLoaderDelegation.class
                .getClassLoader().getParent().getParent()); //null

        /**AppClassLoader->ExtClassLoader-> null(bootstrap class loader 非java类加载器)*/
    }
}
