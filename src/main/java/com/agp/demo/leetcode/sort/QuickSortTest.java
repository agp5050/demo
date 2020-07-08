package com.agp.demo.leetcode.sort;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

@Slf4j
public class QuickSortTest {
    @Test
    public void testSort(){
        int[] ary=generateAry(20,10);
        log.info("ary:{}", JSONArray.toJSONString(ary));
        new QuickSort().sort(ary);
        log.info("ary:{}，after sort.", JSONArray.toJSONString(ary));
    }

        @Test
    public void testSort2(){
        int[] ary=generateAry(20,10);
        log.info("ary:{}", JSONArray.toJSONString(ary));
        new QSort().sort(ary);
        log.info("ary:{}，after sort.", JSONArray.toJSONString(ary));
    }
    @Test
    public void testEquality(){
        int[] ints = generateAry(20, 20);
        log.info("ints after generateAry:{}",JSONArray.toJSONString(ints));
        int[] ints2=new int[ints.length];
        System.arraycopy(ints,0,ints2,0,ints2.length);
        log.info("equality,after arraycopy:{}", Arrays.asList(ints).equals(Arrays.asList(ints2)));
        QSort.sort(ints);
//        new QuickSort().sort(ints2);
        PopSort.sort(ints2);
        log.info("equality,after tow different sort:{}",Arrays.asList(ints).equals(Arrays.asList(ints2)));
        log.info("ints:{}",JSONArray.toJSONString(ints));
        log.info("ints2:{}",JSONArray.toJSONString(ints2));

    }


    @Test
    public void testMulti(){
        for (int i=0;i<100;i++){
            testSort();
        }
    }
    public void testFindTheKBiggest(){
        testSort();

    }

    private int[] generateAry(int max, int length) {
        int[] rst=new int[length];
        Random random=new Random();
        for (int i=0;i<length;i++){
            rst[i]=random.nextInt(max);
        }
        return rst;
    }
}
