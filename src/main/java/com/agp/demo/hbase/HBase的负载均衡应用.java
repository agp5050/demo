package com.agp.demo.hbase;

/**
 * 扩容操作一般分为两个步骤：
 * 首先，需要增加节点并让系统感知到节点加入；
 * 其次，需要将系统中已有节点负载迁移到新加入节点上。
 *
 * 第二步负载迁移在具体实现上需要借助于负载均衡机制。
 *
 * 1. 负载均衡策略
 *      SimpleLoadBalancer策略
 *      StochasticLoadBalancer策略。
 *
 *  SimpleLoadBalancer策略:
 *      这种策略能够保证每个RegionServer的Region个数基本相等
 *      假设集群中一共有n个RegionServer，m个Region，那么集群的平均负载就是average=m/n
 *      集群负载迁移计划就是Region从个数较多的RegionServer上迁移到个数较少的RegionServer上。
 *   种策略简单易懂
 *   读写QPS、数据量大小等因素都没有实际考虑
 *
 *   因为某台RegionServer上的Region全部都是热点数据，
 *   导致90%的读写请求还是落在了这台RegionServer上，
 *   这样显而易见没有达到负载均衡的目的
 *
 *  StochasticLoadBalancer策略
 *      由多种独立负载加权计算的复合值
 *
 *      独立负载包括
 *          •Region个数（RegionCountSkewCostFunction）
 *          •Region负载
 *          •读请求数（ReadRequestCostFunction）
 *          •写请求数（WriteRequestCostFunction）
 *          •Storefile大小（StoreFileCostFunction）
 *          •MemStore大小（MemStoreSizeCostFunction）
 *          •数据本地率（LocalityCostFunction）
 *          •移动代价（MoveCostFunction）
 *    这些独立负载经过加权计算会得到一个代价值，
 *    系统使用这个代价值来评估当前Region分布是否均衡，越均衡代价值越低。
 *    HBase通过不断随机挑选迭代来找到一组Region迁移计划，使得代价值最小。
 *
 *  2. 负载均衡策略的配置
 *      配置文件 hbase.master.loadbalancer.class
 *          ....SimpleLoadbalancer
 *  3. 负载均衡相关的命令
 *      balance_switch  负载均衡开关
 *      balancer  负载均衡执行
 *
 */
public class HBase的负载均衡应用 {
}
