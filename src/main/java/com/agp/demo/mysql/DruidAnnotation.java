package com.agp.demo.mysql;

/**
 * 1 、maxWait  从连接池获取connection的等待时间。 suggest 1000 ms以上，
 * default =0 无限等待，可能会导致请求卡住资源耗尽雪崩
 *
 * 2、connection properties: a、 connectionTimeout 跟数据库建立TCP链接超时时间 suggest:1200
 * b、socketTimeout 通道建立后，网络导致延迟可能假死。 设置为3000 ，3s过后还没有回应，断开链接释放资源。
 *
 * 3、 maxActive 最大连接池数量， 一般20.  太多会导致频繁切换CPU过高，性能降低。 最多加到几十个就可以了。
 *
 */
public class DruidAnnotation {
}
