package com.agp.demo.hbase;

/**
 * Region分裂是HBase最核心的功能之一，是实现分布式可扩展性的基础。
 * HBase中，Region分裂有多种触发策略可以配置，
 * 一旦触发，HBase会寻找分裂点，然后执行真正的分裂操作。
 *
 * 1. Region分裂触发策略
 *      HBase已经有6种分裂触发策略。
 *      每种触发策略都有各自的适用场景，用户可以根据业务在表级别选择不同的分裂触发策略
 *      常见的分裂策略如图8-3所示。
 *
 *      ConstantSizeRegionSplitPolicy ：0.94版本之前默认分裂策略。
 *      表示一个Region中最大Store的大小超过设置阈值
 *      （hbase.hregion.max.filesize）之后会触发分裂。
 *
     *      ConstantSizeRegionSplitPolicy最简单，但是在生产线上这种分裂策略却
     *      有相当大的弊端——分裂策略对于大表和小表没有明显的区分。
     *          阈值（hbase.hregion.max.filesize）设置较大对大表比较友好，
     *          但是小表就有可能不会触发分裂，极端情况下可能就只有1个Region，
     *          这对业务来说并不是什么好事。如果阈值设置较小则对小表友好，
     *          但一个大表就会在整个集群产生大量的Region，
     *          对于集群的管理、资源使用来说都不是一件好事。
 *
 *•IncreasingToUpperBoundRegionSplitPolicy ：
 * 这种分裂策略总体来看和ConstantSizeRegionSplitPolicy思路相同，
 * 一个Region中最大Store大小超过设置阈值就会触发分裂。
 * 但是这个阈值并不像ConstantSizeRegionSplitPolicy是一个固定的值
 * ，而是在一定条件下不断调整，
 *
 * 调整后的阈值大小和Region所属表在当前RegionServer上的Region个数有关系，
 * 调整后的
 * 阈值等于(#regions) *(#regions) * (#regions)* f lush size * 2，
 * 当然阈值并不会无限增大，最大值为用户设置的MaxRegionFileSize
 * 这种分裂策略很好地弥补了ConstantSizeRegionSplitPolicy的短板，能够自适应大表和小表
 *
 * 这种策略并不完美，比如在大集群场景下，很多小表就会产生大量小Region，分散在整个集群中。
 *
    * •SteppingSplitPolicy ：2.0版本默认分裂策略
 *      分裂阈值大小Region所属表在当前RegionServer上的Region个数有关系，
 *      如果Region个数等于1，分裂阈值为f lush size * 2，否则为MaxRegionFileSize。
 *
 *      这种分裂策略对于大集群中的大表、小表会比IncreasingToUpperBoundRegionSplitPolicy
 *      更加友好，小表不会再产生大量的小Region。
 *
 * 还有一些其他分裂策略，比如使用
 * DisableSplitPolicy可以禁止Region发生分裂；
 * 而KeyPref ixRegionSplitPolicy
 * 和DelimitedKeyPrefixRegionSplitPolicy依然依据默认的分裂策略
 *
 * 在用法上，一般情况下使用默认分裂策略即可，
 * 也可以在cf级别设置Region分裂策略，命令为如下：
 *      create 'abc',{NAME=>'cf',SPLIT_POLICY=>'....ConstantSizeRegionSplitPolicy'}
 *
 *2. Region分裂准备工作——寻找分裂点
 *  分裂被触发后的第一件事是寻找分裂点。
 *  所有默认分裂策略对于分裂点的定义都是一致的。
 *  HBase对于分裂点的定义为：
 *  整个Region中最大Store中的最大文件中
 *  最中心 的一个Block的首个rowkey。
 *  另外，HBase还规定，
 *  如果定位到的rowkey是整个文件的首个rowkey或者最后一个rowkey，则认为没有分裂点。
 *最常见的就是待分裂Region只有一个Block，执行split的时候就会无法分裂。
 *
 *3. Region核心分裂流程
 *
 * HBase将整个分裂过程包装成了一个事务，
 * 目的是保证分裂事务的原子性。
 * 整个分裂事务过程分为三个阶段：
     * prepare、
     * execute
     * 和rollback。
 *  if(!st.prepare)) return;
 *  try{
 *      st.execute(this.server,this.server,user);
 *      success=true;
 *  }catch(Exception e){
 *      try{
 *          st.rollback(this.server,this.server);
 *      }
 *  }
 *
 * （1）prepare阶段
     * 在内存中初始化两个子Region，具体生成两个HRegionInfo对象，
     * 包含tableName、regionName、startkey、endkey等。
     * 同时会生成一个transactionjournal，这个对象用来记录分裂的进展
 * （2）execute阶段
 *      这个阶段的步骤如下：
 *      1）RegionServer将ZooKeeper节点/region-in-transition
 *      中该Region的状态更改为SPLITING。
 *
 *      2）Master通过watch节点/region-in-transition检测到Region状态改变，
 *      并修改内存中Region的状态，在Master页面RIT模块可以看到Region执行split的状态信息。
 *
 *      3）在父存储目录下新建临时文件夹.split，保存split后的daughter region信息。
 *
 *      4）关闭父Region。父Region关闭数据写入并触发f lush操作，
 *      将写入Region的数据全部持久化到磁盘。
 *      此后短时间内客户端落在父Region上的请求都会抛出异常NotServingRegionException。
 *
 *      5）在.split文件夹下新建两个子文件夹，称为daughter A、daughter B，
 *      并在文件夹中生成reference文件，分别指向父Region中对应文件。
 *      通过reference文件名就可以知道reference文件指向哪个父Region中的哪个HFile文件
 *      reference文件内容并不是用户数据，而是由两部分构成
 *          其一是分裂点splitkey
 *          其二是一个boolean类型的变量（true或者false）
 *          ，true表示该reference文件引用的是父文件的上半部分（top）
 *          ，false表示引用的是下半部分（bottom）
 *      6）父Region分裂为两个子Region后，
 *      将daughter A、daughter B拷贝到HBase根目录下，形成两个新的Region。
 *      7）父Region通知修改hbase:meta表后下线，不再提供服务。
 *      下线后父Region在meta表中的信息并不会马上删除，
 *      而是将split列、offline列标注为true，并记录两个子Region，
 *      8）开启daughter A、daughter B两个子Region。
 *      通知修改hbase:meta表，正式对外提供服务，参见图8-7。
 *5. Region分裂对其他模块的影响
 *
 *      Region分裂过程因为没有涉及数据的移动，
 *      所以分裂成本本身并不是很高，可以很快完成。
 *
 *      分裂后子Region的文件实际没有任何用户数据，
 *      文件中存储的仅是一些元数据信息——分裂点rowkey等
 *      那么通过reference文件如何查找数据呢？
 *      子Region的数据实际在什么时候完成真正迁移？
 *      数据迁移完成之后父Region什么时候会被删掉？
 *
 *
 *（1）通过reference文件查找数据
 *      1）根据reference文件名（父Region名+HFile文件名）定位到真实数据所在文件路径。
 *      2）根据reference文件内容中记录的两个重要字段确定实际扫描范围。
 *      top字段表示扫描范围是HFile上半部分还是下半部分。
 *      结合splitkey字段可以明确扫描范围为[f irstkey, splitkey)；
 *      如果top为false，表示扫描的是下半部分，
 *
 *（2）父Region的数据迁移到子Region目录的时间
 *      迁移发生在子Region执行Major Compaction时。
 *       根据Compaction原理，从一系列小文件中依次由小到大读出所有数据并写入一个大文件，
 *        完成之后再将所有小文件删掉，因此Compaction本身就是一次数据迁移
 *
 *      分裂后的数据迁移完全可以借助Compaction实现，子Region执行Major Compaction
 *      后会将父目录中属于该子Region的所有数据读出来，并写入子Region目录数据文件中
 *
 * （3）父Region被删除的时间
 *
 *  Master会启动一个线程定期遍历检查所有处于splitting状态的父Region，
 *  确定父Region是否可以被清理。检查过程分为两步：
 *  1）检测线程首先会在meta表中读出所有split列为true的Region，
 *  并加载出其分裂后生成的两个子Region（meta表中splitA列和splitB列）。
 *  2）检查两个子Region是否还存在引用文件，
 *  如果都不存在引用文件就可以认为该父Region对应的文件可以被删除。
 *
 *
 */
public class Region分裂 {
}
