package com.agp.demo.hystrix;

import com.agp.demo.Person;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
//TODO  为什么第二次访问，就开始返回[]，实际command是有结果的啊。？
@RestController
@Slf4j
public class GetPersonController {
    @GetMapping("/getPerson")
    public String getPerson(Integer id){
        GetPersonCommand getPersonCommand = new GetPersonCommand(id);
        //DO HTTP Request to get Person with parameter ID,
        //If no hystrix , high request concurrently will lead all threads used. 进而引起服务不可用，依赖
        //这个服务的其他上游服务也可能导致雪崩。
        Person execute = getPersonCommand.execute();
        return JSON.toJSONString(execute);
    }
    @GetMapping("/getPersonAsync")
    public String getPersonAsync(Integer id){
        GetPersonCommand getPersonCommand = new GetPersonCommand(id);
        //DO HTTP Request to get Person with parameter ID,
        //If no hystrix , high request concurrently will lead all threads used. 进而引起服务不可用，依赖
        //这个服务的其他上游服务也可能导致雪崩。
        Future<Person> queue = getPersonCommand.queue();
        Person person=null;
        try {
             person = queue.get();  //wait until future object executed.
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return JSON.toJSONString(person);
    }
    @GetMapping("/getPersonByName")
    public String getPersonByName(String name){
        GetPersonsCommand command=new GetPersonsCommand(name);
        //立即执行。 如同queue() , execute()一样立即执行。
        /**This eagerly starts execution of the command
         * the same as {@link HystrixCommand#queue()} and {@link HystrixCommand#execute()}*/
        Observable<Person> observe = command.observe();
        List<Person> rst=new ArrayList<>();

        observe.subscribe(new Observer<Person>() {
            @Override
            public void onCompleted() {
                System.out.println("獲取完了所有的用戶");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Person person) {
                System.out.println(person);
//                查找回来后立刻执行semaphore本地隔离，查询cache。
                GetWifeCommand getWifeCommand=new GetWifeCommand(person.getName());
                String wifeName = getWifeCommand.execute();
                System.out.println("person "+person.getName()+" just find his wife : "+wifeName);
                rst.add(person);
            }
        });

        return JSONArray.toJSONString(rst);
    }

    @GetMapping("/getPersonByNameDelay")
    public String getPersonByNameDelay(String name){
        GetPersonsCommand command=new GetPersonsCommand(name);
        /**This lazily starts execution of the command once the  Observable is subscribed to*/
        //这个延迟执行。直到subscribe订阅时才开始执行。
        Observable<Person> personObservable = command.toObservable();
        List<Person> list=new ArrayList<>();
        try {
            //Do something itself...
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        personObservable.subscribe(new Observer<Person>() {
            @Override
            public void onCompleted() {
                System.out.println("获取完毕for："+name);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Person person) {
                list.add(person);
            }
        });
        return JSONArray.toJSONString(list);
    }

    @GetMapping("/getByAnnotation")
    @HystrixCommand(commandKey = "query", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "750")
    }, fallbackMethod = "queryDataTimeOut")
    public String getByAnnotation(@RequestBody Person person){
        System.out.println(person);
        return getString();
    }

    private String getString() {
        try {
            //InterruptedException: sleep interrupted  被Hystrix线程池接管后，超时中断了
            Thread.currentThread().sleep(1700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "null";
    }

    public String queryDataTimeOut(@RequestBody Person conditions) {
        log.error("超时入参：{}", conditions);
        return "超时:"+conditions.toString();
    }
}
