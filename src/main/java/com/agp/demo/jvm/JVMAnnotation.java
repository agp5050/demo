package com.agp.demo.jvm;

/**
 * JAVA内存模型--》原子性、可见性、有序性 -》volatile--》happens-before/内存屏障
 *
 *
 * 可见性， T1更新 a=1的值后， 下次T2读取的时候必须从主内存读取。就是可见性。
 *
 * 原子性T1必须执行完毕 read load use assign store write 之后，T2才能接着执行。
 *
 * 没有原子性T1，T2从主存读取，并执行上面6个命令。
 *
 * 有序性： 代码执行的过程可能会发生指令重排。就是没有有序性。
 * 如果不发生指令重排就是有有序性。
 * flag=false -> prepareResource() --> flag=true --> doSomething...
 * 指令重排后可能还没准备好，就做其他事情了。
 *
 *
 * Happens-before 8条规则，保证特定的情况下指令必须不能重排，必须按照顺序。
 * 指令重排《happens-before《volatile
 */
public class JVMAnnotation {
}
