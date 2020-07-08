package com.agp.demo.leetcode.sort.pairreverse;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *  1->2->3->4->5->6->7  pairReverse 为 2->1->4->3->6->5->7
 */
public class PairReverse {

    public static List<Integer> pairReverse(List<Integer> list){
        int size = list.size();
        for(int i=0;i<size;i=i+2){
            if (i+1<size){
                int tmp=list.get(i);
                list.set(i,list.get(i+1));
                list.set(i+1,tmp);
            }
        }
        return list;
    }

    @Test
    public void testReverse(){

        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        List<Integer> integers2 = new LinkedList<>();
        integers2.add(1);
        integers2.add(2);
        integers2.add(3);
        integers2.add(4);
        integers2.add(5);
        integers2.add(6);
        integers2.add(7);
        System.out.println(pairReverse(integers)); //[2, 1, 4, 3, 6, 5, 7]
        System.out.println(pairReverse(integers2)); //[2, 1, 4, 3, 6, 5, 7]
    }

}
