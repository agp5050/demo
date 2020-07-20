package com.agp.demo.utils;

import org.junit.Assert;
import org.junit.Test;

public class HighestOneAllOne {
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * @param cap
     * @return
     * 正整數最高位肯定是1. 這個方法就是将1像左移1位。然后再移两位再移四位。 逐渐将最高位1的左边全部设定为1.
     * 比如3-》11  7-》111  15-》1111 这样.就是将原来最接近的下一个2的幂次方-1
     */
    static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }
    @Test
    public void test(){
        Assert.assertEquals(tableSizeFor(3),4);
        Assert.assertEquals(tableSizeFor(5),8); //100 -> 111 =7 +1 = 8
        Assert.assertEquals(tableSizeFor(7),8);
    }
}
