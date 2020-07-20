package work.tmp;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LinesToMap {
    public Map<String,Integer> getMap(String lines){
        Map<String,Integer> rst=new HashMap<>();
        Arrays.asList(lines.split("\n")).stream()
                .forEach(item->rst.put(item.trim(),0));
        return rst;
    }
    @Test
    public void test(){
        String str="lst_1day_refuse_cnt\n" +
                "lst_3day_refuse_cnt\n" +
                "lst_7day_refuse_cnt\n" +
                "lst_30day_refuse_cnt\n" +
                "lst_60day_refuse_cnt\n" +
                "lst_90day_refuse_cnt\n" +
                "lst_120day_refuse_cnt\n" +
                "lst_180day_refuse_cnt\n" +
                "lst_360day_refuse_cnt\n";
        System.out.println(JSON.toJSONString(getMap(str)));
    }

    public void test2(){
        String str="customerId\n" +
                "appId\n" +
                "certId\n" +
                "userType\n";

    }
}
