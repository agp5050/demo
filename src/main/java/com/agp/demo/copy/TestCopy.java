package com.agp.demo.copy;

import com.agp.demo.annotation.RightInvoke;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
@Slf4j
public class TestCopy {
    @Test
    public void testHashMapClone(){
        HashMap<String,String> map=new HashMap<>();
        map.put("aa","bb");
        map.put("aa2","bb2");
        HashMap<String, String> clone = (HashMap<String, String>) map.clone();
        System.out.println(clone);
        log.info("map clone equality:{}",map.equals(clone));
    }
    @Test
    public void testArrayCopy(){
        String[] ary=new String[10];
        String[] ary2=new String[2];
        ary[0]="a";
        ary[9]="b";
        System.arraycopy(ary,0,ary2,0,2);
        log.info("clone ary:{}", JSONArray.toJSONString(ary2));
    }
    @RightInvoke("Arrays copyOf copyOfRange 都可以支持超出原数组范围，" +
            "可以自动填充0。左开右闭空间。")
    @Test
    public void testArraysCopyOf(){
        int[] ary=new int[2];
        ary[0]=1;
        ary[1]=2;
        int[] copyOf = Arrays.copyOf(ary, 3);
        log.info("copyOf truncating or padding:{}",JSONArray.toJSONString(copyOf));
        int[] ints = Arrays.copyOfRange(ary, 0, 3);
        log.info("copyOfRange :{} truncating or padding",JSONArray.toJSONString(ints));

        int[] ints2 = Arrays.copyOfRange(ary, 0, 0);
        log.info("copyOfRange :{} from == to",JSONArray.toJSONString(ints2));

    }


}
