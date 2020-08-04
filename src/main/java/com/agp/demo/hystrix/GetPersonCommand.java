package com.agp.demo.hystrix;

import com.agp.demo.Person;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
//HystrixCommand 用來獲取一條數據
//HystrixObservable 設計用來獲取多條數據
public class GetPersonCommand extends HystrixCommand<Person> {
    private Integer id;
    public GetPersonCommand(Integer personID) {
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        this.id=personID;
    }

    @Override
    protected Person run() throws Exception {
        //DO HttpRequest with ( personId) to getPerson
//        Thread.currentThread().sleep(2000);
        Person person=new Person();
        person.setId(id);
        person.setName("test");
        person.setAge(23);
        return person;
    }
}
