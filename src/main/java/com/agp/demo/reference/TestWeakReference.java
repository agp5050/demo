package com.agp.demo.reference;

import com.agp.demo.Person;
import com.agp.demo.annotation.TestIntension;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
@Slf4j
public class TestWeakReference {
    @TestIntension("WeakReference使用场景：有价值cache，容易重新构建，很消耗内存的场景。")
    @Test
    public void testGcCallBack() throws InterruptedException {
        Person per=new Person();
        per.setAge(29);
        per.setName("xxx");
        ReferenceQueue<Person> referenceQueue=new ReferenceQueue<>();
        WeakReference<Person> weakReference=new WeakReference<Person>(per,referenceQueue);
        int i=0;
        while (true){
            if (i>20){
                System.gc();  //weakReference并不是单次GC就会被回收，可能多次GC才会发现，然后回收
                //垃圾收集器可能要运行多次才能找到并释放弱可及对象
            }
            if (weakReference.get()!=null){
                i++;
                System.out.println("weakReference.get {}, loop counts{}"+weakReference.get()+i);
                System.out.println("referenceQueueIf:"+referenceQueue.poll());
            }else {
                log.info("weakReference refer object is collected.");
                System.out.println("referenceQueueElse:"+referenceQueue.poll());
                break;
            }
            Thread.sleep(100);
        }
    }
}
