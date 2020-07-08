package com.agp.demo.thread;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class ThreadTest {
    @Test
    public void constantTest() throws InterruptedException {
        Thread thread=new Thread(){
            public void run(){
                log.info("thread name:{}",this.getName());
                log.info("thread interrupt status:{}",this.isInterrupted());
                log.info("thread group name:{}",this.getThreadGroup().getName());
                log.info("thread group super threads:{}",this.getThreadGroup().getParent()); //threadGroup :System ,priority :10
                log.info("thread is daemon:{}",this.isDaemon());
                log.info("thread pid:{}",this.getId());
                log.info("thread priority:{}",this.getPriority());
                log.info("active count:{}",this.activeCount());
                log.info("thread is alive:{},thread's status:{}",this.isAlive(),this.getState());
                int i=0;
                while (true){
                    i++;
                    try {
                        this.sleep(100);
                        if (i>20) {
                            log.info("thread is interrupted before interrupt:{}",this.isInterrupted());

                            log.info("thread is interrupted after interrupt:{}",this.isInterrupted());
                            log.info("thread status:{}",this.getState());
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        thread.start();
        thread.interrupt();//单独调用interrupt 关闭本thread。
        thread.join(); // 等待join的时候，interrupt也报错，但是还会继续执行等到完成。
        log.info("thread is alive;{}",thread.isAlive());
        log.info("thread status:{}",thread.getState());
    }
}
