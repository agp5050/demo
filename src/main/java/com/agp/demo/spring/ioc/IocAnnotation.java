package com.agp.demo.spring.ioc;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

/**
 * SpringIOC ： spring启动时启动 Spring容器
 */
public class IocAnnotation {
    public static void main(String[] args) {

        ApplicationContext  applicationContext;
        DefaultListableBeanFactory registry;  //实际注册中心。 里面多个concurrentHashMap 存储bean definition等
    }
}
