package com.agp.demo.es;

/**
 *索引文档时，文档会被存储到一个主分片中。如下公式决定文档存储到哪一个分片：
 * shard = hash(routing) % number_of_primary_shards
 * routing 默认值为文档 _id ，也可以自定义，
 * 所有文档API（get、index、update、delete、mget、bulk）都支持 routing 参数。
 * 主分片数在创建索引时就已确定，且不可更改。
 * 否则，主分片数量变化，之前的 routing 值都会无效，文档将会找不到
 *使用路由时需要考虑“数据倾斜”问题
 *
 *
 */

/*启动问题
max virtual memory areas vm.max_map_count [65530] is too low, increase to at least [262144]
sysctl -w vm.max_map_count=262144 临时修改
为了让sysctl所操作的参数永久有效，
需要修改/etc/sysctl.conf(CentOS5 6) 或者 /etc/sysctl.d/*.conf(CentOS7）的配置文件
vm.max_map_count=262144
sysctl -p 生效
 */
/*
max number of threads [3818] for user [es] is too low, increase to at least [4096]

*  soft nproc  4096
*  hard nproc  4096




*/
public class ESAnnotation {
    /*写数据：
    * 1客户端选择一个Node1发送写请求， Node1作为 Coordinating Node
    * （协调节点，所有节点都能当做协调节点，主要用于请求转发）
    * 2协调节点通过 routing 计算，确定文档属于主分片P1 。将写请求转发到主分片P1所在的Node2
    * 3 Node2在主分片上执行写请求，成功后将数据同步到Node1、Node3的副本分片R1
    * 4 一旦所有副本分片都报告成功，Node2向协调节点报告成功，协调节点向客户端报告成功
    *
    * 具体单台Node写原理：
    * 1、执行写：请求到达主分片时，先写内存 buffer ；同时数据写入 translog 日志文件。此时数据无法搜索
    * 2、refresh：buffer 快满时，每隔1秒数据 refresh 到一个新的 segment file 中。
    * 在 buffer 数据 refresh  到 segment 之前，数据会先进入 os cache ，并且清空 buffer
    * 。只要进入了 os cache ，数据就能被搜索到，因此说是ES准实时的。
    * 3、commit：不断重复 buffer 和 translog ， buffer 随着 refresh 清空，
    * translog 保留。 translog 达到一定长度后，
    * 触发 commit （也叫 flush ，默认每30分钟执行一次）操作，并清空 translog 。
    *  commit  也是先到 os cache ，每隔5秒持久化到磁盘。
    *
    * 4、merge：  buffer 每 refresh 一次就会产生一个 segment file ，
    * 此时定期执行 merge ，合并成一个大的segment ；segment file 多到一定程度也会自动触发 merge 。
    * 5、删除和更新：删除操作 commit 时会生成一个 .del 文件，
    * 将文档标识为deleted状态；更新操作 commit 时将原文档标识为deleted，
    * 并新写入一条数据。 merge 的时候会物理删除deleted状态的文档。
    *
    *
    * 读过程：
    *
    * 客户端选择一个Node1发送读请求， Node1作为 Coordinating Node
协调节点通过 routing 计算，确定文档属于分片1，
* 此时3个节点都包含分片1（主分片和副本分片都可以用作读操作），
* 根据轮询以达到负载均衡，将此次读请求转发到Node2
Node2将文档返回给协调节点Node1，然后将文档返回给客户端
    *
    * */
}
