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
    public static void getJson(String dataOutputs){
        System.out.println("JSON出参映射：");
        System.out.println(toJson(dataOutputs));
    }
    public static void getSql(String dataOutputs,String table,String conditionFields,String conditions){
        System.out.println("Cql语句：");
        System.out.println(toSql(dataOutputs,table,conditionFields,conditions));
    }
    public static void getMysql(String dataOutputs,String table,String conditionFields,String conditions){
        System.out.println("Mysql语句：");
        System.out.println(toMysql(dataOutputs,table,conditionFields,conditions));
    }



    public static String toSql(String dataOutputs,String table,String conditionFields,String conditions){
        StringBuilder sb = getStringBuilder(dataOutputs);

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
    public static String toMysql(String dataOutputs,String table,String conditionFields,String conditions){
        StringBuilder sb = getStringBuilder(dataOutputs);

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
            conditionMarks=addMysqlSomething(conditionMarks);
            String conditionField=fields[i];
            sb.append(conditionField)
                    .append(" = ")
                    .append(conditionMarks);
            if (!last) sb.append(" and ");

        }

        return sb.toString();

    }

    private static StringBuilder getStringBuilder(String dataOutputs) {
        String[] split = dataOutputs.split("\n");
        StringBuilder sb = new StringBuilder("select ");
        Arrays.asList(split).stream()
                .map(item -> item.split(":")[0].trim())
                .map(item -> item.substring(1, item.length() - 1))
                .forEach(item -> sb.append(item + ","));
        sb.deleteCharAt(sb.length() - 1);
        return sb;
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

    public static Map<String,Object> toIntMapWithInputFields(String dataOutputs,String inputFields,String inputValues){
        Map<String, Integer> stringIntegerMap = toIntMap(dataOutputs);
        Map<String,Object> rstMap=new HashMap<>();
        rstMap.putAll(stringIntegerMap);
        String[] split = inputFields.split(",");
        String[] splitValue = inputValues.split(",");
        if (split.length!=splitValue.length) throw new RuntimeException("参数和值不匹配");
        for (int i=0;i<split.length;i++){
            rstMap.put(split[i],splitValue[i]);
        }
        return rstMap;
    }

    public static void getInsertDataJSON(String dataOutputs,String inputFields,String inputValues){
        Map<String, Object> stringObjectMap = toIntMapWithInputFields(dataOutputs, inputFields, inputValues);
        System.out.println("Cql插入数据语句：");
        System.out.println("'"+JSON.toJSONString(stringObjectMap)+"'");
    }
    public static void getOutputs(String dataRawOutputs){
        System.out.println("出参列表：");
        System.out.println(listOutputs(dataRawOutputs));
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
    private static String addMysqlSomething(String conditionMarks) {
        return ":"+conditionMarks;
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

    public static void getInputList(String inputs){
        System.out.println("入参列表：");
        System.out.println(inputs);
    }

    public static void getInputsJson(String inputs){
        System.out.println("JSON入参列表：");
        Map<String,String> rst=new HashMap<>();
        Arrays.asList(inputs.split(",")).stream()
                .forEach(item->rst.put(item,item));
        System.out.println(JSON.toJSONString(rst));
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
    @Test
    public void testSameCustomerInvite(){
        String str="        \"lst_1day_mobile_device_cut_cnt\":0,\n" +
                "        \"lst_7day_mobile_device_cut_cnt\":0,\n" +
                "        \"lst_30day_mobile_device_cut_cnt\":0,\n" +
                "        \"lst_180day_mobile_device_cut_cnt\":0";
        getJson(str);
        getInsertDataJSON(str,"mobile,device_id","454545,7878");
        getSql(str,"fk_sc_kanjia_mobile_device_stat","mobile,device_id","mobile,udid");
        getOutputs(str);
        getInputList("mobile,udid");
        getInputsJson("mobile,udid");
    }

    @Test
    public void testSameCertIdSameDeviceIdInvite(){
        String str="        \"lst_1day_cert_device_cut_cnt\":0,\n" +
                "        \"lst_7day_cert_device_cut_cnt\":0,\n" +
                "        \"lst_30day_cert_device_cut_cnt\":0,\n" +
                "        \"lst_180day_cert_device_cut_cnt\":0";
        getJson(str);
        getInsertDataJSON(str,"cert_id,device_id","52134912365121101,454545");
        getSql(str,"fk_sc_kanjia_cert_device_stat","cert_id,device_id","cardNo,udid");
        getOutputs(str);
        getInputList("cardNo,udid");
        getInputsJson("cardNo,udid");
    }

    @Test
    public void testSameActivityRiskNumber(){
        String str="        \"task_cut_member_risk_level_high_cnt\":2,\n" +
                "        \"task_cut_member_risk_level_middle_cnt\":5,\n" +
                "        \"task_cut_member_risk_level_low_cnt\":4";
        getJson(str);
        getInsertDataJSON(str,"launch_task_id","454545");
        getSql(str,"fk_sc_kanjia_risk_level","launch_task_id","task_number");
        getOutputs(str);
        getInputList("task_number");
        getInputsJson("task_number");
    }

    @Test
    public void testSameDeviceLogonDifferentAccounts(){
        String str=" \"device_diff_login_cut_cnt\": 2";
        getJson(str);
        getInsertDataJSON(str,"device_id","454545");
        getSql(str,"fk_sc_kanjia_login_diff_member","device_id","udid");
        getOutputs(str);
        getInputList("udid");
        getInputsJson("udid");
    }

    @Test
    public void testRecentHourSameMobileSameDeviceInvite(){
        String str="        \"lst_mobile_device_cut_cnt_30min\":0,\n" +
                "        \"lst_mobile_device_cut_cnt_60min\":0,\n" +
                "        \"lst_mobile_device_cut_cnt_180min\":0";
        String inputs="udid,phone";
        String inputsFields="device_id,phone_no";
        getJson(str);
        getInsertDataJSON(str,inputsFields,"4545,18611965012");
        getMysql(str,"record_cut_price",inputsFields,inputs);
        getOutputs(str);
        getInputList(inputs);
        getInputsJson(inputs);
    }
    @Test
    public void testRecentHourSameCardNoSameDeviceInvite(){
        String str="        \"lst_cert_device_cut_cnt_30min\":0,\n" +
                "        \"lst_cert_device_cut_cnt_60min\":0,\n" +
                "        \"lst_cert_device_cut_cnt_180min\":0";
        String inputs="udid,cardNo";
        String inputsFields="device_id,cert_id";
        getJson(str);
        getInsertDataJSON(str,inputsFields,"4545,454545");
        getMysql(str,"record_cut_price",inputsFields,inputs);
        getOutputs(str);
        getInputList(inputs);
        getInputsJson(inputs);
    }

    @Test
    public void testSameDeviceSameCommodity(){
        String str="        \"lst_device_goods_cut_cnt_30min\":0,\n" +
                "        \"lst_device_goods_cut_cnt_60min\":0,\n" +
                "        \"lst_device_goods_cut_cnt_180min\":0";
        String inputs="udid,taskId";
        String inputsFields="device_id,launch_id";
        getJson(str);
        getInsertDataJSON(str,inputsFields,"454545,4545");
        getMysql(str,"record_cut_price",inputsFields,inputs);
        getOutputs(str);
        getInputList(inputs);
        getInputsJson(inputs);
    }

    public static void main(String[] args) {

    }
}
