package com.agp.demo.rpc.dubbo;

/**
 * 对于大型企业来说，内部子系统较多、接口非常多的情况下，RPC框架的好处就显示出来了，
 * 首先就是长链接，不必每次通信都要像http一样去3次握手什么的，减少了网络开销；
 * 其次就是RPC框架一般都有注册中心，有丰富的监控管理；
 * 发布、下线接口、动态扩展等，对调用方来说是无感知、统一化的操作。
 */
public class RPCVsHttpAnnotation {
}
