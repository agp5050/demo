package com.agp.demo.springboot;

import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;

/**
 * BeanFactory:
 *      The root interface for accessing a Spring bean container.
 *      This is the basic client view of a bean container;
 *      further interfaces such as {@link ListableBeanFactory} and
 *      ConfigurableBeanFactory are available for specific purposes.
 *
 *      This interface is implemented by objects that hold a number of bean definitions,
 *  * each uniquely identified by a String name. Depending on the bean definition,
 *  * the factory will return either an independent instance of a contained object
 *  * (the Prototype design pattern), or a single shared instance (a superior
 *  * alternative to the Singleton design pattern
 *
 *  BeanFactory is a central registry
 *  * of application components, and centralizes configuration of application
 *  * components (no more do individual objects need to read properties files,
 *  * for example)
 *
 *  如果是层级结构的，可能会查询父层级是否包涵某个beanDefinition. 子层级高于父层级优先级。
 *  all of the
 *  * operations in this interface will also check parent factories if this is a
 *  * {@link HierarchicalBeanFactory}. If a bean is not found in this factory instance,
 *  * the immediate parent factory will be asked. Beans in this factory instance
 *  * are supposed to override beans of the same name in any parent factory.
 *
 *
 *
 * FactoryBean:
 *      A FactoryBean is defined in a bean style, but the object exposed for bean
 *  * references ({@link #getObject()}) is always the object that it creates.
 *
 *  FactoryBean是Factory类型的Bean，它本身可以被装载到BeanFactory容器里面。
 *  主要是配置一些springboot内部的一些工厂类。
 *
 *
 *
 */
public class BeanFactory和FactoryBean {
}
