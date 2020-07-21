package com.agp.demo.leetcode.sort.compute;

import org.junit.Test;

public class CursorMoveAndAutoIncrement {
    @Test
    public void test(){
        int a=2;
        int a1=2;
        //     4      a=3 -> a=4  a+a
        //     4   << a + a
        //左边 a1直接取值 不计算。 先算右边。  4 << 4+4
        int c1= a1++ << ++a1 + ++a1;
        System.out.println(c1);
        // 3 << 4+5  3<<9 。 << 左右两边都是 计算公式，先计算左边 再计算右边   ++ a 取出来值为 3  << 4 + 5
        int b= ++a << ++a + ++a;
        System.out.println(b); //1024
        System.out.println(b);
        int d=1;
        int c=d++;
        //d++ 和 ++d 是一样的最终都是讲d+1 . 但是如果前面有赋值操作。 d++先赋值后自己++。 ++d先++ 后赋值给前面指针。
        System.out.println(c);
        System.out.println(d);
        System.out.println(3<<9); //3 * 2^9
//        1536-1024 = 512  2^10+2^9  2^9*3

    }


}
