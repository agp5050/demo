package com.agp.demo.DoubleColorLottery;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class RandomSelectTest {
    @Test
    public void testRandomSelect(){
        int[] ints = RandomSelect.randomSelect();
        log.info("randomSelect:{}", JSONArray.toJSONString(ints));
    }
    @Test
    public void multiTestRandomSelect(){
        for (int i=0;i<100;i++){
            testRandomSelect();
        }
    }

}
