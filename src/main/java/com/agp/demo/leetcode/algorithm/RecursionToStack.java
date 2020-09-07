package com.agp.demo.leetcode.algorithm;

import java.util.Stack;

/**
 * 递归转为循环出入栈
 */
public class RecursionToStack {
    public static void main(String[] args) {
        System.out.println(factorialRecursive(3));
    }

    public static int factorialRecursive(int max){
        if (max==1) return 1;
        return max*factorialRecursive(max-1);
    }

    /**
     * @param max
     * @return
     * 利用栈
     */
    public static int factorialStack(int max){
        Stack<Integer> stack=new Stack<>();
        stack.push(max);
        int result=1;
        while (!stack.isEmpty()){
            Integer pop = stack.pop();
            result=result*pop;
            if (pop>1) stack.push(pop-1);
        }
        return result;
    }
}
