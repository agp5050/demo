package com.agp.demo.leetcode.sort;

import java.util.Arrays;
import java.util.List;

public class BinarySearch<T extends Comparable<T>> {
    public static <T extends Comparable<T>> T binarySearch(T[] ary,T target){
        int start=0;
        int end=ary.length-1;
        while (start<=end){
            int middle=(start+end)/2;
            T guess=ary[middle];//中间值
            if (guess.compareTo(target)==0) return guess;
            if (guess.compareTo(target)>0) { //中间值大于目标值
                end=middle-1;
            }else {  //中间值小于target
                start=middle+1;
            }
        }
        return null;
    }

    public static <T extends Comparable<T>> T binarySearchRecursive(T[] ary,T target,int start,int end){
        if (start>end) return null;
        int middle=(start+end)/2;
        T guess=ary[middle];
        int compare = guess.compareTo(target);
        if (compare==0) return guess;
        else if(compare>0){
            return binarySearchRecursive(ary,target,start,middle-1);
        }else {
            return binarySearchRecursive(ary,target,middle+1,end);
        }
    }

    public static void main(String[] args) {
        String s = binarySearch("1 3 5 8 9".split(" "), "8");
        String s1 = binarySearchRecursive("1 3 5 8 9".split(" "), "8",0,4);
        List<Integer> collect = Arrays.asList(1, 3, 5, 7, 11, 13, 17);
        Integer[] objects=new Integer[collect.size()];
        collect.toArray(objects);
        Integer integer = binarySearch(objects, 11);
        Integer integer1 = binarySearchRecursive(objects, 11, 0, 6);
        System.out.println(integer);
        System.out.println(integer1);
        System.out.println(s);
        System.out.println(s1);
    }
}
