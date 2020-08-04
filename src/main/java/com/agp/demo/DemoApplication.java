package com.agp.demo;

import com.agp.demo.hystrix.HystrixRequestContextServletFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;

@EnableHystrix
@EnableCaching
@SpringBootApplication
public class DemoApplication {
    @Bean
    public FilterRegistrationBean hystrixRequestContextFilter(){
        FilterRegistrationBean filterRegistrationBean=new FilterRegistrationBean(new HystrixRequestContextServletFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
