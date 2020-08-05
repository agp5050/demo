package com.agp.demo.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;

public class GetWifeCommand extends HystrixCommand<String> {
    private String name;
    protected GetWifeCommand(String name) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("getWife"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("getWifeCommand"))
                //With ThreadPool Properties s
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.defaultSetter().withCoreSize(20))
                //With Command Properties set
                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.defaultSetter()
                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE

                ).withExecutionIsolationSemaphoreMaxConcurrentRequests(15)  //使用semaphore 时最大的并发通过15个线程。
                        .withFallbackIsolationSemaphoreMaxConcurrentRequests(100) //限制并发访问降级逻辑。 防止把降级逻辑shutdown。
                 .withCircuitBreakerRequestVolumeThreshold(20)  //时间段内，如果没达到20个，all fail也不会触发
                        .withCircuitBreakerErrorThresholdPercentage(60) //时间段内，如果没有达到60%触发率也是不触发短路. default 50
                        .withCircuitBreakerSleepWindowInMilliseconds(10000) //default 5000ms ，触发短路后，尝试休眠多久后再
                        //半开断路器 half_open
                        .withExecutionTimeoutInMilliseconds(750)  //默认1s超时。不是两秒了。。
                )
        )
        ;
        this.name=name;
    }

    @Override
    protected String run() throws Exception {
        return LocalCache.getWife(name);
    }
}
