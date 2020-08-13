package com.agp.demo;

public class ABC {
    private static   String outer="aaa";
    public static class BCD{
        private String name;
        private Integer age;
        {
            System.out.println("outer:"+outer+"");
        }
    }
    public static void main(String[] args) {
       new ABC.BCD();
    }
}
