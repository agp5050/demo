package com.agp.demo.spring;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Spring配置类，让其扫描到我们的FactoryBean
 *
 *
 */
@Configuration
@ComponentScan
class AppConfig {
}
/**
 * 自定义的FactoryBean
 */
@Component
 class CustomerFactoryBean implements FactoryBean<UserService> {
    /*被springcontext管理后，直接提供的是getObject里面的bean实例。
    * getObject我们可以自定义生成bean的逻辑规则。 */
    /*上面component、@service等一般都是调用构造方法默认创建*/
    @Override
    public UserService getObject() throws Exception {
        return new UserService();
    }

    @Override
    public Class<?> getObjectType() {
        return UserService.class;
    }
}

/**
 * FactoryBean创建的类，注意没有@Component注解
 * 也就是说，该类没有向spring容器中注册
 */
 class UserService {

}

public class Main {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        // 打印Spring容器中所有的bean的定义
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(beanDefinitionName);
        }
        System.out.println(" ");
        System.out.println(" ");
// 获取UserService的实例
        System.out.println(applicationContext.getBean(UserService.class));
        // 这种方式获取userService会报错
        // System.out.println(applicationContext.getBean("userService"));
        // 获取我们自定义的FactoryBean
        System.out.println(applicationContext.getBean("customerFactoryBean"));        System.out.println(applicationContext.getBean(UserService.class) ==  applicationContext.getBean("customerFactoryBean"));
        // 获取CustomFactoryBean类本身的实例
        System.out.println( applicationContext.getBean("&customerFactoryBean"));
    }
}

/*打印：
 * com.agp.demo.spring.UserService@245b4bdc
 * com.agp.demo.spring.UserService@245b4bdc
 * true
 * com.agp.demo.spring.CustomerFactoryBean@2c767a52
* */