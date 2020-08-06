package com.agp.demo.cls;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public class DynamicClassLoader extends ClassLoader {
    /**
     * 类文件字节流.
     */
    private byte[] classBytes;
    /*
    * loadClass 一般JVM执行，用来根据名称去所有的包下面找这个文件名字的类加载。
    * 所以不一般不用来自定义加载class。
    * */
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }

    /*
    *
    * 这个findClass 就会用定义的 name。 和class本体byte【】构建class对象。
    * 这个一般用来自定义加载类。
    *
    */

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return defineClass(name,classBytes,0,classBytes.length);
    }

    public static void main(String[] args) throws ClassNotFoundException {
        //@Autowired
        DefaultListableBeanFactory defaultListableBeanFactory=new DefaultListableBeanFactory();


        DynamicClassLoader loader=new DynamicClassLoader();
        Class<?> aaa = loader.findClass("aaa");


        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(aaa);

        //动态注册bean
        defaultListableBeanFactory.registerBeanDefinition("beanName", beanDefinitionBuilder.getBeanDefinition());
    }
}
