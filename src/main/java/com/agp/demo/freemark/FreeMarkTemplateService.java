package com.agp.demo.freemark;

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
}
