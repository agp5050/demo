package com.agp.demo.spi;

import com.agp.demo.annotation.Result;
import org.junit.Test;

import java.util.ServiceLoader;

/**
 * serviceLoader
 * SPI机制locates and
 *  * loads service providers deployed in the run time environment at a time of an
 *  * application's choosing. Application code refers only to the service, not to
 *  * service providers, and is assumed to be capable of choosing between multiple
 *  * service providers (based on the functionality they expose through the service),
 *  * and handling the possibility that no service providers are located.
 *  配置化，不需要专门制定特定的类，只要调用ServiceLoader。相当于加了一个套件，一个代理。
 *  配置化的代理。   定位和加载服务。
 */
public class TestSpi {
    @Test
    @Result("I'm java developer.\n" +
            "I'm python developer.")
    public void test(){
        ServiceLoader<Developer> serviceLoader=ServiceLoader.load(Developer.class);
        serviceLoader.forEach(Developer::sayHi);

        //ServiceLoader --> A facility to load implementations of a service(interface).
    }
}
