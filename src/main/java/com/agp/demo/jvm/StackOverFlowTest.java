package com.agp.demo.jvm;
// 查看JVM配置 -Xss512k
public class StackOverFlowTest {
    private int stack_dep=1;
    public void stackLeak() {
        System.out.println(stack_dep++);
        stackLeak();
    }
    public static void main(String[] args) {
        StackOverFlowTest obj=new StackOverFlowTest();
        try {
            obj.stackLeak();
        } catch (Exception e) {
            System.out.println(obj.stack_dep);
            throw e;
//            e.printStackTrace();
        }
    }
}
