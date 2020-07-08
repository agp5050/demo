package com.agp.demo.leetcode.sort;

public class QSort {
    public static void sort(int[] ary){
        dosort(ary,0,ary.length-1);
    }
    public static void sort(int[] ary,int start,int end){
        dosort(ary,start,end);
    }

    private static void dosort(int[] ary, int start, int end) {
        if (start>=end)return;
        int key=ary[start];
        int l=start;
        int r=end;
        while (l<r){
            while (ary[r]>=key && r>l){
                r--;
            }
            while (ary[l]<=key && r>l){
                l++;
            }
            swapValue(ary,l,r);
        }
        if (ary[l]<key){
            ary[start]=ary[l];
            ary[l]=key;
        }
        dosort(ary,start,l-1);
        dosort(ary,l+1,end);
    }

    private static void swapValue(int[] ary, int l, int r) {
        int tmp=ary[l];
        ary[l]=ary[r];
        ary[r]=tmp;
    }
}
