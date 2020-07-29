package com.agp.demo.cache;

import com.agp.demo.annotation.TestIntension;
import org.springframework.cache.annotation.Cacheable;

import javax.servlet.http.HttpServletRequest;

public class SpringCacheTest {
    @TestIntension("当标记在一个类上时则表示该类所有的方法都是支持缓存的。" +
            "对于一个支持缓存的方法，Spring会在其被调用后将其返回值缓存起来，" +
            "以保证下次利用同样的参数来执行该方法时可以直接从缓存中获取结果，" +
            "而不需要再次执行该方法。")
    @Cacheable
    class UseCass{
        @Cacheable
        public String query(HttpServletRequest request){
            return "";
        }

    }
}
