package com.agp.demo;

public class PrintCurrentMilliTime {
    public static void main(String[] args) {
        long ti = System.currentTimeMillis();
        long milliYearFactor=1000*60*60*24*365L;
        System.out.println("elapsed "+(ti/milliYearFactor)+" year since 1970");
        System.out.println(System.currentTimeMillis());
    }
}
