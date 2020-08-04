package com.agp.demo.hystrix;

import com.netflix.hystrix.HystrixThreadPool;
import lombok.extern.slf4j.Slf4j;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.internal.operators.BlockingOperatorToFuture;
import rx.plugins.RxJavaPlugins;
import rx.schedulers.Schedulers;

/**
 * //TODO 繼續學習
 */
@Slf4j
public class HystrixTest {
    public static void main(String[] args) {
        RxJavaPlugins.getInstance().getObservableExecutionHook().onCreate(null);
        BlockingOperatorToFuture a;
        HystrixThreadPool.HystrixThreadPoolDefault d;  //
        String TAG="{}";
        Observable
                .create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        log.error(TAG, "===create: " + Thread.currentThread().getName());
                        subscriber.onNext("1");
                    }
                })
                .map(new Func1<String, Integer>() {
                    @Override
                    public Integer call(String s) {
                        log.error(TAG, "===String -> Integer: " + Thread.currentThread().getName());
                        return Integer.valueOf(s);
                    }
                })
                .flatMap(new Func1<Integer, Observable<String>>() {
                    @Override
                    public Observable<String> call(final Integer integer) {
                        log.error(TAG, "===Integer->Observable: " + Thread.currentThread().getName());
                        return Observable.create(new Observable.OnSubscribe<String>() {
                            @Override
                            public void call(Subscriber<? super String> subscriber) {
                                log.error(TAG, "===Observable<String> call: " + Thread.currentThread().getName());
                                for (int i = 0; i < integer; i++) {
                                    subscriber.onNext(i + "");
                                }
                                subscriber.onCompleted();
                            }
                        });
                    }
                })
                .map(new Func1<String, Long>() {
                    @Override
                    public Long call(String s) {
                        log.error(TAG, "===String->Long: " + Thread.currentThread().getName());
                        return Long.parseLong(s);
                    }
                })
                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Long aLong) {
                        log.error(TAG, "===onNext: " + Thread.currentThread().getName());
                    }
                });
    }
}
