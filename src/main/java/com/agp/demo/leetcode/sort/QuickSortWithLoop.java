package com.agp.demo.leetcode.sort;

import com.alibaba.fastjson.JSONArray;

import java.util.Stack;


public class QuickSortWithLoop {
    public static void main(String[] args) {
        int[] ary=new int[]{1, 3, 2, 7, 4, 5, 9, 11, 10, 19, 15, 13, 20,3,1,7,18};
        sort(ary);
        System.out.println(JSONArray.toJSONString(ary));
    }
    public static void sort(int[] ary){
        doSort(ary,0,ary.length-1);
    }

    private static void doSort(int[] ary, int start, int end) {
        Stack<Integer> stack=new Stack<>();
        stack.push(start);
        stack.push(end);
        while (!stack.isEmpty()){
            int innerEnd = stack.pop();
            int r=innerEnd;
            int innerStart   = stack.pop();
            int l=innerStart;
            int key=ary[l];
            while (l<r){
                while (ary[r]>=key && r > l){
                    r--;
                }
                while (ary[l]<=key && r > l){
                    l ++;
                }
                //出现了ary[r]> ary[l]的值
                if (r>l){
                    swap(ary,r,l);
                }
            }
            //r==l时跳出循环，不能遗漏这个值的比较。
            if (ary[l]<key){
                swap(ary,innerStart,l);
            }
                if (innerStart<l-1){
                    stack.push(innerStart);
                    stack.push(l-1);
                }

                if (l+1<innerEnd){
                    stack.push(l+1);
                    stack.push(innerEnd);
                }




        }
    }

    private static void swap(int[] ary,int r,int l){
        int tmp=ary[r];
        ary[r]=ary[l];
        ary[l]=tmp;
    }
}
