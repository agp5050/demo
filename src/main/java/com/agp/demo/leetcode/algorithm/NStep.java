package com.agp.demo.leetcode.algorithm;

/**
 * N台阶问题：
 * 一次只能1步或者2步。 n阶台阶。
 * 求一共有多少种选择能走到n。
 * 最后一步可以是n-1 走一步到达。
 * 或者 n-2 走一步到达。
 * 所以 f(n)=f(n-1)+f(n-2) 走到最后两步的所有可能的和。
 * 0->n
 */
public class NStep {
    public static int getTotalSteps(int n){
        if (n==0) return 0;
        if (n==1) return 1;
        if (n==2) return 2;
        return getTotalSteps(n-1)+getTotalSteps(n-2);
    }

    public static void main(String[] args) {
        System.out.println(getTotalSteps(3));  // 1 1 1/ 1 2/ 2 1/
        System.out.println(getTotalSteps(4));  //f(3)+f(2)=3+2;
        System.out.println(getTotalSteps(5));  //f(4)+f(3)= 3+ 5=8;
    }
}
