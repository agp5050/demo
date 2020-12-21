package com.agp.demo.springboot;

import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;

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
 *if classLoader ==null ==> ClassLoader.getSystemResources("META-INF/spring.factories")
 *
 * SpringFactoriesLoader加载器加载指定ClassLoader下面的 所有META-INF/spring.factories文件，
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
 *AutoConfigurationImportFilter
 * # Auto Configuration Import Filters    ===》 AutoConfigurationImportFilter
 * org.springframework.boot.autoconfigure.AutoConfigurationImportFilter=\
 * org.springframework.boot.autoconfigure.condition.OnBeanCondition,\
 * org.springframework.boot.autoconfigure.condition.OnClassCondition,\
 * org.springframework.boot.autoconfigure.condition.OnWebApplicationCondition
 *
 * 			for (AutoConfigurationImportFilter filter : this.filters) {
 * 				boolean[] match = filter.match(candidates, this.autoConfigurationMetadata);
 * 				for (int i = 0; i < match.length; i++) {
 * 					if (!match[i]) {
 * 						candidates[i] = null;
 * 						skipped = true;
 *                                        }                *            }
 * 			}
 *
 *
双循环对3个Condition注解都包涵的，配置类才是有效的
对自动配置组件列表进行再次过滤，
过滤条件为该列表中自动配置类的注解得包含在OnBeanCondition、OnClassCondition和OnWebApplicationCondition中指定的注解，
依次包含@ConditionalOnBean、@ConditionalOnClass和@ConditionalOnWebApplication。


在完成了以上步骤的过滤、筛选之后，
我们最终获得了要进行自动配置的类的集合，在将该集合返回之前，
在AutoConfigurationImportSelector类中完成的最后一步操作就是相关事件的封装和广播，相关代码如下。

将筛选出的自动配置类集合和被排除的自动配置类集合封装成AutoConfigurationImportEvent事件对象，
并传入该事件对象通过监听器提供的onAutoConfigurationImportEvent方法，最后进行事件广播。

List<AutoConfigurationImportListener> listeners = getAutoConfigurationImportListeners();
if (!listeners.isEmpty()) {
AutoConfigurationImportEvent event = new AutoConfigurationImportEvent(this, configurations, exclusions);
for (AutoConfigurationImportListener listener : listeners) {
invokeAwareMethods(listener);
listener.onAutoConfigurationImportEvent(event);
}
}



 *
 *
 *
 *
 */

public class AutoConfigurationImportSelector源码解析 {

    SpringApplicationAdminJmxAutoConfiguration t;
    AopAutoConfiguration a;
    CassandraDataAutoConfiguration c;

    public static void main(String[] args) {


    }
}
