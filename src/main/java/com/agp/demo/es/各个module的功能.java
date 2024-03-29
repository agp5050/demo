package com.agp.demo.es;

/**
 * Cluster
 *  Cluster模块是主节点执行集群管理的封装实现，管理集群状态，
 *  维护集群层面的配置信息。主要功能如下：
 *      · 管理集群状态，将新生成的集群状态发布到集群所有节点。
 *      · 调用allocation模块执行分片分配，决策哪些分片应该分配到哪个节点
 *      · 在集群各节点中直接迁移分片，保持数据平衡。
 *
 * allocation
 *  封装了分片分配相关的功能和策略，包括主分片的分配和副分片的分配，
 *  本模块由主节点调用。
 *  创建新索引、集群完全重启都需要分片分配的过程。
 *
 *Discovery
 *  发现模块负责发现集群中的节点，以及选举主节点。
 *  当节点加入或退出集群时，主节点会采取相应的行动。
 *  从某种角度来说，发现模块起到类似ZooKeeper的作用，选主并管理集群拓扑。
 *
 * gateway
 *  负责对收到Master广播下来的集群状态（cluster state）数据的持久化存储，
 *  并在集群完全重启时恢复它们。
 *
 * Indices
 *  索引模块管理全局级的索引设置，
 *  不包括索引级的（索引设置分为全局级和每个索引级）。
 *  它还封装了索引数据恢复功能。
 *  集群启动阶段需要的主分片恢复和副分片恢复就是在这个模块实现的。
 *
 * HTTP
 *  HTTP模块允许通过JSON over HTTP的方式访问ES的API,HTTP模块本质上是完全异步的，
 *  这意味着没有阻塞线程等待响应。
 *  使用异步通信进行 HTTP 的好处是解决了 C10k 问题（10k量级的并发连接）。
 *
 *  在部分场景下，可考虑使用HTTP keepalive以提升性能。注意：不要在客户端使用HTTPchunking。
 *
 * Transport
 *  传输模块用于集群内节点之间的内部通信。
 *  从一个节点到另一个节点的每个请求都使用传输模块。
 *  如同HTTP模块，传输模块本质上也是完全异步的。
 *  传输模块使用 TCP 通信，每个节点都与其他节点维持若干 TCP 长连接。
 *  内部节点间的所有通信都是本模块承载的。
 * Engine
 *  Engine模块封装了对Lucene的操作及translog的调用，
 *  它是对一个分片读写操作的最终提供者。
 *  ES使用Guice框架进行模块化管理。Guice是Google开发的轻量级依赖注入框架（IoC）。
 *
 *
 *
 *
 */
public class 各个module的功能 {
}
