package com.agp.demo.freemark;

import com.agp.demo.annotation.RightInvoke;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.HashMap;

public class FreeMarkTemplateService {



    private static Configuration freeMarkerConfiguration=new Configuration();

    /**
     * 根据入参解析模板
     * @param templateName              模板名称
     * @param templateContext           模板
     * @param model                     入参model类
     * @return  afterTemplateContext    解析后的模板
     */
    public static String parsingTemplate(String templateName, String templateContext, Object model) throws IOException, TemplateException {

        StringTemplateLoader stringLoader = new StringTemplateLoader();
        stringLoader.putTemplate(templateName, templateContext);
        freeMarkerConfiguration.setTemplateLoader(stringLoader);
        Template template = freeMarkerConfiguration.getTemplate(templateName);
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }

    /**
     * 31	select count(1) as count002 from Vabc:aaa where CUSTOMERID = :customerId
     */
    @Test
    public void test() throws IOException, TemplateException {
        String templateName="1";
        String templateContext="select count(1) as count002 from Vabc_<#if dayMod = 1>1<#else>0</#if> where 1=1 <#if customerId?? && customerId!= \"\"> and CUSTOMERID = :customerId </#if>";
        HashMap<String,Object> model=new HashMap<String,Object>();
        model.put("dayMod",0);
        model.put("customerId","customerIdV");
        System.out.println(parsingTemplate(templateName,templateContext,model));
    }
    @RightInvoke("要学习Freemarker的语法才可以。将文本model和java对象组合输出为String")
    @Test
    public void testArray() throws IOException, TemplateException {
        String[] split = "1,2".split(",");
        String sql="select abc_${ary[0]} from abcT";
        HashMap<String,Object> model=new HashMap<String,Object>();
        model.put("ary",split);
        System.out.println(parsingTemplate("2",sql,model));
    }
    @Test
    public void testIf() throws IOException, TemplateException {
        String sql="select * from abc where 1 = 1 <#if customerId ?? && customerId != \"\"> and customer_id = :customerId</#if>";
        HashMap<String,Object> model=new HashMap<String,Object>();
        model.put("customerId","");
        System.out.println(parsingTemplate("2",sql,model));
    }
    @RightInvoke("这种SQL中条件都进行了判空，如果对方空调，这样会出现selectAll情况。爆掉内存，所以要根据情况加上limit n")
    @Test
    public void testUndercharge() throws IOException, TemplateException {
        String sql="select app_id,cert_id,customer_id,user_type from rep_wk_cs_reduce_repay where 1=1 <#if appId ?? && appId != \"\"> and app_id =:appId</#if><#if certId ?? && certId != \"\"> and cert_id =:certId</#if><#if customerId ?? && customerId != \"\"> and customer_id =:customerId</#if><#if userType ?? && userType != \"\"> and user_type =:userType</#if> limit 10";
        HashMap<String,Object> model=new HashMap<String,Object>();
        model.put("customerId","a");
        model.put("appId","a");
        model.put("certId","a");
        model.put("userType","a");
        System.out.println(parsingTemplate("2",sql,model));
    }
}
