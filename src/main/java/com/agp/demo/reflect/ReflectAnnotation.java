package com.agp.demo.reflect;

import java.lang.reflect.Array;

public class ReflectAnnotation {
    public static void main(String[] args) {
        int[] a=new int[10];
        int[] intsReflect = (int[]) Array.newInstance
                (a.getClass().getComponentType(), 20);
        System.out.println(intsReflect.hashCode()+" : "+intsReflect.length);
        System.out.println(IN1.num);
    }
    static class IN1{
        static int num;
        static {
            System.out.println("static "+num);
        }
    }
}
