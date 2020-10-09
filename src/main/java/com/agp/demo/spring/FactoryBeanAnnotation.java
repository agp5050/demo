package com.agp.demo.spring;

/**
 * FactoryBean和BeanFactory虽然名字很像，
 * 但是这两者是完全不同的两个概念，
 * 用途上也是天差地别。BeanFactory是一个Bean工厂，
 * 我们可以简单理解为它就是我们平常所说的Spring容器，
 * 它完成了Bean的创建、自动装配等过程，
 * 存储了创建完成的单例Bean。
 *
 *
 * 用户可以通过实现该接口定制实例化Bean的逻辑
 *
 */
/*而FactoryBean通过名字看，我们可以猜出它是Bean，
但它是一个特殊的Bean，一个工厂Bean，用来生成其他Bean*/


/*private Object doGetObjectFromFactoryBean(final FactoryBean<?> factory, final String beanName)
      throws BeanCreationException */
/*else {
         object = factory.getObject();//生成对象
      }*/

/*用来生成复杂逻辑的bean，制定bean初始化*/
public class FactoryBeanAnnotation {
}
