package com.agp.demo.json;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Slf4j
public class JsonTest {
    @Test
    public void test(){
       String a=" [\"type\":\"G_01\",\"day_num\":\"\",\"start_date\":\"01\",\"end_date\":\"31\",\"start_time\":\"21:50:00\",\"end_time\":\"23:59:59\"]";
        Object parse = JSONArray.parse(a);
        log.info("parse:{}",parse);
        Map<String,String> map=new HashMap<>();
        map.put("aa","bb");
        map.put("aa2","bb2");
        map.put("aa3","bb3");

    }
}
