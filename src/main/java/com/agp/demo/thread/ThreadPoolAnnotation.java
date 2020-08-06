package com.agp.demo.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Executors工具类创建
 * ThreadPoolExecutor  ：works <HashSet>,
 *
 * 1. size 《 corePoolSize   addWorker(command, true)
 * 2. size 》 corePoolSize  workQueue.offer(command) 《根据不同的Queue实现可以入队成功或者失败》
 * 3. Queue加入失败后， addWorker(command, false) --》这时以 maximumPoolSize为限制，如果
 * 低于限制就直接添加Worker。   每个worker添加后都会启动thread。直接执行。
 *4. keepAliveTime 超出corePoolSize的worker，工作完idle的时间。
 * wc >= (core ? corePoolSize : maximumPoolSize)
 *
 * AbortPolicy 默认满了之后拒绝策略。 生产环境可以自定义，比如将reject策略设置为落磁盘，等待
 * 一定时间后再提交。
 *
 * 突然宕机--队列和执行中的线程丢失， --》 可以execute（Runnable） 同时入库，并将记录改为已提交状态，runnable执行完毕改为。已完成状态
 * 重启后，从已提交里面恢复之前任务。
 *
 *
 * addWorker(command, false)
 *
 */
public class ThreadPoolAnnotation {

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(3);
    }
}
