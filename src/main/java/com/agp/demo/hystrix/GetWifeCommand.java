package com.agp.demo.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class GetWifeCommand extends HystrixCommand<String> {
    private String name;
    protected GetWifeCommand(String name) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("getWife"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.defaultSetter()
                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE))
        )
        ;
        this.name=name;
    }

    @Override
    protected String run() throws Exception {
        return LocalCache.getWife(name);
    }
}
