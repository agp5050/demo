package com.agp.demo.hystrix;

import com.agp.demo.Person;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class GetPersonsCommand extends HystrixObservableCommand<Person> {
    private String name;
    public GetPersonsCommand(String name ) {
        super(HystrixCommandGroupKey.Factory.asKey("exampleObservable"));
        this.name=name;
    }

    @Override
    protected Observable construct() {
        return Observable.unsafeCreate(new Observable.OnSubscribe<Person>() {
            @Override
            public void call(Subscriber<? super Person> subscriber) {
                try{
                        Person p1=new Person(1,name,23);
                        Person p2=new Person(2,name,33);
                        subscriber.onNext(p1);
                        subscriber.onNext(p2);
                        subscriber.onCompleted();
                }catch (Exception e){
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }
}
