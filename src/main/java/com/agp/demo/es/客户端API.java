package com.agp.demo.es;

/**
 * 客户端对集群进行读写操作时
 * 可以选择
     * REST接口、
     * Java REST API，
     * 或者JavaAPI。
 * Java REST API是对原生REST接口的封装。
 * REST接口、Java REST API使用9200端口通信，
 * 采用JSON over HTTP方式，
 *
 * Java API使用9300端口通信，数据序列化为二进制。
 *
 * ES不是高QPS的应用，写操作非常消耗CPU资源，因此写操作属于比较长的操作，
 * 聚合由于涉及数据量比较大，延迟也经常到秒级，查询一般也不密集。
 *
 * 。因此RPC框架的效率没有那么高的要求。后续Java API将逐渐被Java REST API取代。
 * 官方计划从ES 7.0开始不建议使用Java API，并且从8.0版本开始完全移除。
 *
 *
 */
public class 客户端API {
}
