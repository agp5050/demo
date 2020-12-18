package com.agp.demo.springboot;

/**
 * Spring Boot最核心的功能就是自动配置
 * 功能的实现都是基于“约定优于配置”的原则。
 *
 * 使用Spring Boot时，我们只需引入对应的Starters，
 *  Spring Boot启动时便会自动加载相关依赖，
 * 配置相应的初始化参数，以最快捷、简单的形式对第三方软件进行集成，
 *  这便是Spring Boot的自动配置功能。
 *
 *
 * 自动化配置核心原理图如，2-1所示
     * Spring Boot通过@EnableAutoConfiguration注解开启自动配置，
     * 加载spring.factories中注册的各种AutoConfiguration类，
     * 当某个AutoConfiguration类满足其注解@Conditional指定的生效条件
     * （Starters提供的依赖、配置或Spring容器中是否存在某个Bean等）时，
     * 实例化该AutoConfiguration类中定义的Bean（组件等），
     * 并注入Spring容器，就可以完成依赖框架的自动配置。
 *
 *
 * ·@EnableAutoConfiguration：
     * 该注解由组合注解@SpringBootApplication引入，完成自动配置开启，
     * 扫描  各个jar包下  的spring.factories文件，
     * 并加载文件中注册的AutoConfiguration类等。
 *
 * ·spring.factories：
     * 配置文件，位于jar包的META-INF目录下，
     * 按照指定格式注册了自动配置的AutoConfiguration类。
     * spring.factories也可以包含其他类型待注册的类。
     * 该配置文件不仅存在于Spring Boot项目中，
     * 也可以存在于自定义的自动配置（或Starter）项目中。
 * ·AutoConfiguration类：
     * 自动配置类，代表了Spring Boot中一类以XXAutoConfiguration命名的自动配置类。
     * 其中定义了三方组件集成Spring所需初始化的Bean和条件。
 * ·@Conditional：
     * 条件注解及其衍生注解，在AutoConfiguration类上使用，
     * 当满足该条件注解时才会实例化AutoConfiguration类。
 * ·Starters：
     * 三方组件的依赖及配置，Spring Boot已经预置的组件。
     * Spring Boot默认的Starters项目往往只包含了一个pom依赖的项目。
     * 如果是自定义的starter，该项目还需包含spring.factories文件、
     * AutoConfiguration类和其他配置类。
 *
 * 入口类和@SpringBootApplication注解
     * 在Spring Boot入口类（除单元测试外）中，
     * 唯一的一个注解就是@SpringBootApplication。
     * 它是Spring Boot项目的核心注解，用于开启自动配置，
     * 准确说是通过该注解内组合的@EnableAutoConfiguration开启了自动配置。
 *
 * ·scanBasePackages：指定扫描的基础package，用于激活@Component等注解类的初始化。
 * ·scanBasePackageClasses：扫描指定的类，用于组件的初始化。
 *
 *
 * 未使用Spring Boot的情况下
 * ，Bean的生命周期由Spring来管理，然而 Spring无法 自动配置 @Configuration注解的类
 *
 *
 *
 * @EnableAutoConfiguration
 *  的主要功能是启动Spring应用程序上下文时进行自动配置
 *  它会尝试猜测并配置项目可能需要的Bean。
 *  自动配置通常是基于项目classpath中引入的类和已定义的Bean来实现的。
 *
 *  被@EnableAutoConfiguration注解的类所在package还具有特定的意义
 *  通常会被作为扫描注解@Entity的根路径。
 *  这也是在使用@SpringBootApplication注解时 需要将被注解的类 放在顶级package下的原因，
 *  如果放在较低层级，它所在package的同级或上级中的类就无法被扫描到。
 *
 *  而对于入口类和其main方法来说，并不依赖@SpringBootApplication注解或@EnableAuto-Configuration注解，
 *  也就是说该注解可以使用在其他类上，而非入口类上。
 *
 *
 */
public class SpringBoot核心运行原理 {
}
