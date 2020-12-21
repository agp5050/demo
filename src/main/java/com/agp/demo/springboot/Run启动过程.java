package com.agp.demo.springboot;

import org.springframework.web.context.ConfigurableWebApplicationContext;

/**
 * WebApplicationType.deduceFromClasspath();
 *
 *
 *
 * "javax.servlet.Servlet",
 * 			"org.springframework.web.context.ConfigurableWebApplicationContext"
 *
 * 	如果上面这两个类存在 则判断为WebApplicationType.SERVLET  servlet Web服务器
 * 	并class.forName加载对应的这两个类
 *
 * 加载spring.factories文件里面的ApplicationContextInitializer 对应的类，生成类对象Collection
 * 		setInitializers((Collection) getSpringFactoriesInstances(ApplicationContextInitializer.class));
 *
 * 加载spring.factories文件里面的ApplicationListener 对应的类，生成类对象Collection
 *		setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));
 *
 *
 * 打印Banner，如果没有 spring.banner.image.location IMageBander
 * 或者spring.banner.location --》default banner.txt  这两个environment参数没有指定，或者指定位置不存在文件
 * 就用默认banner ：
 * DEFAULT_BANNER = new SpringBootBanner();
 * 源码如下：
 * 	private Banner getBanner(Environment environment) {
 * 		Banners banners = new Banners();
 * 		banners.addIfNotNull(getImageBanner(environment));
 * 		banners.addIfNotNull(getTextBanner(environment));
 * 		if (banners.hasAtLeastOneBanner()) {
 * 			return banners;
 *                }
 * 		if (this.fallbackBanner != null) {
 * 			return this.fallbackBanner;
 *        }
 * 		return DEFAULT_BANNER;
 *
 *
 *
 *
 * 	**
 * 	 * The class name of application context that will be used by default for web
 * 	 * environments.
 *          ** 	public static final String DEFAULT_SERVLET_WEB_CONTEXT_CLSS = "org.springframework.boot."
        * 			+ "web.servlet.context.AnnotationConfigServletWebServerApplicationContext";


 * DEFAULT_CONTEXT_CLASS = "org.springframework.context."
 * 			+ "annotation.AnnotationConfigApplicationContext"
 *
 *
 * DefaultListableBeanFactory  默认的Bean定义注册中心 和ConfigurableListableBeanFactory 实现类
 *
 *
 */
public class Run启动过程 {

    ConfigurableWebApplicationContext a;
}
