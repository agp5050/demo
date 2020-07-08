package com.agp.demo.leetcode.sort;

public class QuickSort {
    public void sort(int[] ary){
        doQuickSort(ary,0,ary.length-1);
    }

    private void doQuickSort(int[] ary, int start, int end) {
        if (start>=end) return;
        int middle=start;
        int inStart=start;
        int inEnd=end;
        while (inEnd>inStart){
            if (middle==inStart){  //遍历后面
                if (ary[inEnd]<= ary[middle]) inEnd--;
                else {
                    swapValue(ary,middle,inEnd);
                    middle=inEnd;
                    inStart++;
                }
            }else {  //从前面遍历
                if (ary[inStart]>=ary[middle]) inStart++;
                else {
                    swapValue(ary,middle,inStart);
                    middle=inStart;
                    inEnd--;
                }

            }
        }
        doQuickSort(ary,start,middle-1);
        doQuickSort(ary,middle+1,end);
    }

    private void swapValue(int[] ary, int middle, int inEnd) {
        int tmp=ary[middle];
        ary[middle]=ary[inEnd];
        ary[inEnd]=tmp;
    }
}
