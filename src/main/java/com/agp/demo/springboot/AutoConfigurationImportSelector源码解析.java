package com.agp.demo.springboot;

/**
 * @EnableAutoConfiguration的关键功能是通过@Import注解导入的ImportSelector来完成的
 *
 * 是自动配置功能的核心实现者。
 *
 * @Import注解位于spring-context项目内，主要提供导入配置类的功能。
 *
 * @Import的作用和xml配置中<import/>标签的作用一样，
 * 我们可以通过@Import引入@Configuration注解的类，
 * 也可以导入实现了ImportSelector或ImportBeanDefinitionRegistrar的类，
 * 还可以通过@Import导入普通的POJO
 * （将其注册成Spring Bean，导入POJO需要Spring 4.2以上版本）。
 *
 * @Import
 * 的许多功能都需要借助接口ImportSelector来实现
 * ImportSelector决定可引入哪些@Configuration。ImportSelector接口源码如下。
 * public interface ImportSelector{
 *     String[] selectImports(AnnotationMetadata importingClassMetadata);
 * }
 * 参数AnnotationMetadata内包含了被@Import注解的类的注解信息
 * 在selectImports方法内
 *      可根据具体实现决定返回 哪些配置类的全限定名 ，将结果以字符串数组的形式返回。
 *
 *
 * 如果实现了接口ImportSelector的类的同时又实现了以下4个Aware接口
 * 那么Spring保证在调用ImportSelector之前会先调用Aware接口的方法
 * 这4个接口为：
     * EnvironmentAware、
     * BeanFactoryAware、
     * BeanClassLoaderAware
     * ResourceLoaderAware。
 * AutoConfigurationImportSelector的源代码中就实现了这4个接口
 *
 * AutoConfigurationImportSelector并没有直接实现ImportSelector接口，
 * 而是实现了它的子接口DeferredImportSelector
 *会在所有的@Configuration类加载完成之后再加载返回的配置类
 * DeferredImportSelector的加载顺序可以通过@Order注解或实现Ordered接口来指定。
 *
 *
 *
 * 当AutoConfigurationImportSelector被@Import注解引入之后，
 * 它的selectImports方法会被调用并执行其实现的自动装配逻辑。
 *
 *
 *
 *通过Spring Core提供的SpringFactoriesLoader类可以读取spring.factories文件中注册的类
 *
 * classLoader.getResources("META-INF/spring.factories"):Enumeration<URL>
 *if classLoader ==null ==> ClassLoader.getSystemResources("")
 *
 * SpringFactoriesLoader加载器加载指定ClassLoader下面的所有META-INF/spring.factories文件，
 * 并将文件解析内容存于Map<String,List<String>>内。
 * 然后，通过loadFactoryNames传递过来的class的名称从Map中获得该类的配置列表
 *
 * 因为程序默认加载的是ClassLoader下面的所有META-INF/spring.factories文件中的配置，
 * 所以难免在不同的jar包中出现重复的配置。
 * 我们可以在源代码中使用Set集合数据不可重复的特性进行去重操作。
 *
 * 在spring-boot-autoconfigure中默认配置了3个筛选条件，
     * OnBeanCondition、
     * OnClassCondition
     * OnWebApplicationCondition，
 * 它们均实现了AutoConfigurationImportFilter接口。
 *
 *
 *
 *
 *
 *
 */
public class AutoConfigurationImportSelector源码解析 {
}
