package com.agp.demo.DoubleColorLottery;

import com.agp.demo.leetcode.sort.QSort;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * 双色球 33选6红球，16选一篮球 均不重复
 */
public class RandomSelect {
    public static final int RED_LIMIT=33;
    public static final int BLUE_LIMIT=16;
    public static final Random RANDOM=new Random();

    public static int[] randomSelect(){
        Set<Integer> set=new HashSet<>();
        int[] rst=new int[7];
        for (int i=0;i<7;i++){
            if (i<6){ //红球
                int i1;
                do{
                    i1 = RANDOM.nextInt(RED_LIMIT) + 1;
                }while (!set.add(i1));
                rst[i]=i1;
            }else {
                QSort.sort(rst,0,5);
                rst[i]=RANDOM.nextInt(BLUE_LIMIT)+1;
            }

        }
        return rst;
    }

}
