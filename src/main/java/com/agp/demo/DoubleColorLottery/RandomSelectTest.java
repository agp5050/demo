package com.agp.demo.DoubleColorLottery;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class RandomSelectTest {
    @Test
    public void testRandomSelect(){
        int[] ints = RandomSelect.randomSelect();
        log.info("randomSelect:红球:{},{},{},{},{},{}，蓝球:{}", ints[0],ints[1],ints[2],ints[3],ints[4],ints[5],ints[6]);
    }
    @Test
    public void multiTestRandomSelect(){
        for (int i=0;i<100;i++){
            testRandomSelect();
        }
    }

}
