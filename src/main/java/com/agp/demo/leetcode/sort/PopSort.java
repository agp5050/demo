package com.agp.demo.leetcode.sort;

public class PopSort {
    public static void sort(int[] nums){
        for (int i=0;i<nums.length-1;i++){ //执行次数
            int tmp;
            for (int j=0;j+1<nums.length-i;j++){//每次POP起点， 终点。
                if (nums[j]>nums[j+1]) {
                    tmp=nums[j+1];
                    nums[j+1]=nums[j];
                    nums[j]=tmp;
                }
            }
        }
    }
}
