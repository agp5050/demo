package com.agp.demo.utils;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.util.*;

public class DataToJsonSql {
    public static String toJson(String dataOutputs){
        String[] split = dataOutputs.split("\n");
        Map<String,String> rstMap=new HashMap<>();
        Arrays.asList(split).stream()
                .map(item->item.split(":")[0].trim())
                .map(item->item.substring(1,item.length()-1))
                .forEach(item->rstMap.put(item,item));
        return JSON.toJSONString(rstMap);
    }

    public static String toSql(String dataOutputs,String table,String conditionFields,String conditions){
        String[] split = dataOutputs.split("\n");
        StringBuilder sb=new StringBuilder("select ");
        Arrays.asList(split).stream()
                .map(item->item.split(":")[0].trim())
                .map(item->item.substring(1,item.length()-1))
                .forEach(item->sb.append(item+","));
        sb.deleteCharAt(sb.length()-1);

        sb
                .append(" from ")
                .append(table)
                .append(" where ");
        String[] fields = conditionFields.split(",");
        String[] fieldMarks = conditions.split(",");
        int lnt;
        if ((lnt=fields.length)!=fieldMarks.length) return "";
        for (int i=0;i<lnt;i++){
            boolean last=(i==lnt-1);
            String conditionMarks=fieldMarks[i];
            conditionMarks=trimStr(conditionMarks);
            conditionMarks=addSomething(conditionMarks);
            String conditionField=fields[i];
            sb.append(conditionField)
                    .append(" = ")
                    .append(conditionMarks);
            if (!last) sb.append(" and ");

        }

        return sb.toString();

    }
    public static Map<String,Integer> toIntMap(String dataOutputs){
        String[] split = dataOutputs.split("\n");
        Map<String,Integer> map=new HashMap<>();
        Arrays.asList(split).stream()
                .map(item->item.split(":")[0].trim())
                .map(item->item.substring(1,item.length()-1))
                .forEach(item->map.put(item,0));
        return map;
    }

    public static String listOutputs(String dataRawOutputs){
        String[] split = dataRawOutputs.split("\n");
        StringBuilder sb=new StringBuilder();
        Arrays.asList(split).stream()
                .map(item->item.split(":")[0].trim())
                .map(item->item.substring(1,item.length()-1))
                .forEach(item->sb.append(item).append(","));
       return sb.substring(0,sb.length()-1);
    }
    public static String getInputJson(String inputs){
        String[] split = inputs.split(",");
        Map<String,String> map=new HashMap<>();
        Arrays.asList(split).stream()
                .forEach(item->map.put(item,item));
        return JSON.toJSONString(map);
    }
    private static String addSomething(String conditionMarks) {
        return "#"+conditionMarks+"#";
    }

    private static String trimStr(String conditionMarks) {
        while (conditionMarks.startsWith("#")){
            conditionMarks=conditionMarks.substring(1);
        }
        while (conditionMarks.endsWith("#")){
            conditionMarks=conditionMarks.substring(0,conditionMarks.length()-1);
        }
        return conditionMarks;
    }
    @Test
    public void testInvete(){
        String str="        \"lst_1day_1to3hour_invite_cut_cnt\":0,\n" +
                "        \"lst_1day_2to4hour_invite_cut_cnt\":0,\n" +
                "        \"lst_1day_0to5hour_invite_cut_cnt\":0,\n" +
                "        \"lst_7day_1to3hour_invite_cut_cnt\":0,\n" +
                "        \"lst_7day_2to4hour_invite_cut_cnt\":0,\n" +
                "        \"lst_7day_0to5hour_invite_cut_cnt\":0,\n" +
                "        \"lst_30day_1to3hour_invite_cut_cnt\":0,\n" +
                "        \"lst_30day_2to4hour_invite_cut_cnt\":0,\n" +
                "        \"lst_30day_0to5hour_invite_cut_cnt\":0,\n" +
                "        \"lst_180day_1to3hour_invite_cut_cnt\":0,\n" +
                "        \"lst_180day_2to4hour_invite_cut_cnt\":0,\n" +
                "        \"lst_180day_0to5hour_invite_cut_cnt\":0";
        String s = toJson(str);
        System.out.println(s);
        System.out.println(toSql(str,"fk_sc_kanjia_invite_share_stat","cert_id,launch_task_id","cardNo,launchId"));
        System.out.println(listOutputs(str));
    }
    @Test
    public void testShare(){
        String str="        \"lst_1day_1to3hour_click_share_cut_cnt\":0,\n" +
                "        \"lst_1day_2to4hour_click_share_cut_cnt\":0,\n" +
                "        \"lst_1day_0to5hour_click_share_cut_cnt\":0,\n" +
                "        \"lst_7day_1to3hour_click_share_cut_cnt\":0,\n" +
                "        \"lst_7day_2to4hour_click_share_cut_cnt\":0,\n" +
                "        \"lst_7day_0to5hour_click_share_cut_cnt\":0,\n" +
                "        \"lst_30day_1to3hour_click_share_cut_cnt\":0,\n" +
                "        \"lst_30day_2to4hour_click_share_cut_cnt\":0,\n" +
                "        \"lst_30day_0to5hour_click_share_cut_cnt\":0,\n" +
                "        \"lst_180day_1to3hour_click_share_cut_cnt\":0,\n" +
                "        \"lst_180day_2to4hour_click_share_cut_cnt\":0,\n" +
                "        \"lst_180day_0to5hour_click_share_cut_cnt\":0";
        String s = toJson(str);
        System.out.println(s);
        System.out.println(toSql(str,"fk_sc_kanjia_invite_share_stat","cert_id,launch_task_id","cardNo,launchId"));
        System.out.println(listOutputs(str));
        System.out.println(getInputJson("cardNo,launchId"));
    }
    @Test
    public void testJoinMap(){
        String str="        \"lst_1day_1to3hour_invite_cut_cnt\":0,\n" +
                "        \"lst_1day_2to4hour_invite_cut_cnt\":0,\n" +
                "        \"lst_1day_0to5hour_invite_cut_cnt\":0,\n" +
                "        \"lst_7day_1to3hour_invite_cut_cnt\":0,\n" +
                "        \"lst_7day_2to4hour_invite_cut_cnt\":0,\n" +
                "        \"lst_7day_0to5hour_invite_cut_cnt\":0,\n" +
                "        \"lst_30day_1to3hour_invite_cut_cnt\":0,\n" +
                "        \"lst_30day_2to4hour_invite_cut_cnt\":0,\n" +
                "        \"lst_30day_0to5hour_invite_cut_cnt\":0,\n" +
                "        \"lst_180day_1to3hour_invite_cut_cnt\":0,\n" +
                "        \"lst_180day_2to4hour_invite_cut_cnt\":0,\n" +
                "        \"lst_180day_0to5hour_invite_cut_cnt\":0";
        String str2="        \"lst_1day_1to3hour_click_share_cut_cnt\":0,\n" +
                "        \"lst_1day_2to4hour_click_share_cut_cnt\":0,\n" +
                "        \"lst_1day_0to5hour_click_share_cut_cnt\":0,\n" +
                "        \"lst_7day_1to3hour_click_share_cut_cnt\":0,\n" +
                "        \"lst_7day_2to4hour_click_share_cut_cnt\":0,\n" +
                "        \"lst_7day_0to5hour_click_share_cut_cnt\":0,\n" +
                "        \"lst_30day_1to3hour_click_share_cut_cnt\":0,\n" +
                "        \"lst_30day_2to4hour_click_share_cut_cnt\":0,\n" +
                "        \"lst_30day_0to5hour_click_share_cut_cnt\":0,\n" +
                "        \"lst_180day_1to3hour_click_share_cut_cnt\":0,\n" +
                "        \"lst_180day_2to4hour_click_share_cut_cnt\":0,\n" +
                "        \"lst_180day_0to5hour_click_share_cut_cnt\":0";
        Map<String, Integer> map = toIntMap(str);
        map.putAll(toIntMap(str2));
        System.out.println(JSON.toJSONString(map));
    }

    public static void main(String[] args) {

    }
}
