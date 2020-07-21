package com.agp.demo.leetcode.sort.heapsort;



public class HeapSort<T extends Comparable<T>> {
    public static <T  extends Comparable<T>> void heapSort(T[] ary,boolean ascend){
        if (ary.length<=1) return;
        //执行n-1次最大最小堆顶排序。
        for (int i=0;i<ary.length-1;i++){
            //标记最尾部的下标，以便后续ary[0] 和ary[tail]做交换。
            int tail=ary.length-1-i;
            //每次从最尾部执行->0 遍历和对应的father节点进行比较
            for (int j=tail;j>0;j--){
                int fatherIndex=findFatherIndex(j);
                //如果升序排序，需要构建最大顶堆
                if (ascend) {
                   if (ary[fatherIndex].compareTo(ary[j])<0){
                       swap(ary,fatherIndex,j);
                   }
                }else {
                    if (ary[fatherIndex].compareTo(ary[j])>0){
                        swap(ary,fatherIndex,j);
                    }
                }

            }
            //构建最大最小堆栈后，将顶点的数据和最后面的一个进行数值交换。
            swap(ary,0,tail);
        }


    }

    private static <T extends Comparable<T>> void swap(T[] ary, int fatherIndex, int j) {
        T tmp=ary[fatherIndex];
        ary[fatherIndex]=ary[j];
        ary[j]=tmp;
    }

    private static int findFatherIndex(int j) {
        if (j%2==0) return j/2 - 1;
        else return j/2;
    }
}
