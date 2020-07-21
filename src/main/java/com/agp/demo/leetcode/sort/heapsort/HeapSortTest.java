package com.agp.demo.leetcode.sort.heapsort;

import com.alibaba.fastjson.JSONArray;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class HeapSortTest {
    @Test
    public void test(){
        List<Integer> integers = Arrays.asList(1, 3, 5, 2, 7, 4, 3, 2);
        Integer[] integers1 = integers.toArray(new Integer[integers.size()]);
        HeapSort.heapSort(integers1,true);
        System.out.println(JSONArray.toJSON(integers1));
        HeapSort.heapSort(integers1,false);
        System.out.println(JSONArray.toJSON(integers1));
    }
}
