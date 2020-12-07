package com.agp.demo.hbase;

/**
 * HBase使用Coprocessor机制，使用户可以将自己编写的程序运行在RegionServer上。
 * 但在部分特殊应用场景下，使用Coprocessor可以大幅提升业务的执行效率。
 * 比如，业务需要从HBase集群加载出来几十亿行数据进行求和运算或是求平均值运算，
 *
 *
 *6.4.1 Coprocessor分类
     * Observer
     * 和Endpoint
 * Observer：
 *  Observer Coprocessor类似于MySQL中的触发器。
 *  Observer Coprocessor提供钩子使用户代码在特定事件发生之前或者之后得到执行
 *  想在调用get方法之前执行你的代码逻辑，可以重写如下方法：
 *      void preGetOp
 *  如果想在调用get方法之后执行你的代码逻辑，可以重写postGet方法：
 *      void postGetOp
 *  在当前HBase系统中，提供了4种Observer接口：
 *  • RegionObserver，主要监听Region相关事件，比如get、put、scan、delete以及f lush等。
 *  • RegionServerObserver，主要监听RegionServer相关事件，
 *  比如RegionServer启动、关闭，或者执行Region合并等事件。
 *  • WALObserver，主要监听WAL相关事件，比如WAL写入、滚动等。
 *  • MasterObserver，主要监听Master相关事件，比如建表、删表以及修改表结构等。
 *
 *
 * Endpoint Coprocessor类似于MySQL中的存储过程
 * Endpoint Coprocessor允许将用户代码下推到数据层执行。
 * 一个典型的例子就是上文提到的计算一张表（设计大量Region）的平均值或者求和，
 * 可以使用Endpoint Coprocessor将计算逻辑下推到RegionServer执行。
 * 通过Endpoint Coprocessor，用户可以自定义一个客户端与RegionServer通信的RPC调用协议，
 * 通过RPC调用执行部署在服务器端的业务代码。
 *
 * Observer Coprocessor执行对用户来说是透明的，
 * 只要HBase系统执行了get操作，对应的preGetOp就会得到执行
 *
 * 而Endpoint Coprocessor执行必须由用户显式触发调用。
 *
 * 6.4.2 Coprocessor加载
 *
 *两种方式加载到RegionServer ：一种是通过配置文件静态加载；一种是动态加载。
 * 静态加载Coprocessor需要重启集群，使用动态加载方式则不需要重启。
 *
 * 动态加载当前主要有3种方式
 * 方式1：使用shell。
 * 1）disable表，代码如下：
 *  disable test_name
 * 2）修改表schema，代码如下：
 *  alter test_name , METHOD=> 'table_att', 'Coprocessor' =>' hdfs://.../jar | abc.ObserverTest
 *
 * 3）enable表，代码如下：
 *  enable test_name
 *方式2：使用HTableDescriptor的setValue()方法。
 *
 String path="hdfs://.../coprocessor.jar";

 TableDescriptorBuilder test = TableDescriptorBuilder.newBuilder(TableName.valueOf("test"));
 TableDescriptorBuilder.ModifyableTableDescriptor tableDescriptor = (TableDescriptorBuilder.ModifyableTableDescriptor) test.build();
 tableDescriptor.setValue("Coprocessor$1",path+"|"+"mainClassFullName"+"|"+ Coprocessor.PRIORITY_USER);

 方式3：使用HTableDescriptor的addCoprocessor()方法。
 *
 */
public class Coprocessor机制 {
}
