package com.agp.demo.hbase;

import java.util.HashMap;

/**
 * 1. 分布式架构，多个regionServer 分布式管理数据，分布式执行各个nosql数据操作
 * 2.分布式数据存储、自动数据分片     （存储到各个region上面，region达到一定程度会分裂成两个）
 * 3. 集成hdfs 作为分布式文件存储系统
 * 4.强一致读写 ，写成功立马可以读
 * 5. 高可用，任何一个regionserver挂掉也仍然可以读，也不会导致数据丢失，其他服务器可以接管他的工作
 * 6.  支持map reduce、spark计算引擎 抽取和存储数据
 * 7.支持web界面对hbase集群进行运维和管理
 */
/*
* 适用场景：
* 1，海量数据  增量多， 不要事物 ，简单查询
* */


/*
*数据模型：
*
* */
public class HBaseAnnotation {
    /*启动方式1： bin/hbase-daemon.sh start master
    * bin/hbase-daemon.sh start regionserver*/

    /*启动方式2：bin/start-hbase.sh
    * bin/stop-hbase.sh*/
    /*/bin/hbase 客户端  create 'abc','info'  第一个是表名，第二个是列族名*/
    /*列所有表 list*  describe abc 详细信息/
    /*插入命令put 'student','1001', 'info:sex', male'    1001 rowKey  */
    /*常用命令 scan 'abc'*/
    /*Cell -> {rowkey ,column family:column,version}  version 使用timestamp作为区分*/
    /*Namespace 类似于DB，表所在DB    --》 list_namespace ,  create_namespace 'agp' */
    /*指定命名空间建表  create 'agp:test','info'*/

    /**hbase:meta是hbase的元数据表，记录各个region所在副服务器。 --》那meta表在哪个regionserver， zookeeper
     * 专门的节点/hbase/meta-region-server里面存储了 meta表所在节点信息*/
    /*根据Hbase的读取流程，可以发现不是即席查询，主要是用来统计分析用的*/

    /*MemStore写缓存    BlockCache读缓存  查询数据时先从写缓存查，然后读缓存查询，最后才从StoreFile 也就是HFile（hadoop file）
    * 先写回到blockcache里面再返回给客户端。*/
    /*merge  128 limit内存， 3个merge小文件进行合并， 超过256M进行拆分region*/
    /*api   Table  Get-》封装rowKey  Table.get(get)*/
    /*api Put->封装rowkey， put.addColumn(f, qualifier,value 全部是bytes) Table.put(put)*/
    /*api Result.rawCells 遍历 Bytes.toString( ....) clone的都是字节码
     CellUtil.cloneValue(cell)-kvs  cloneRow->rowkey  cloneFamily->f cloneQualifier()*/
    /*bin/hbase  mapredcp  -> 展示向hadoop lib目录copy哪些jar包过去。*/
    /*hadoop-env.sh里面加入 export HADOOP_CLASSPATH=$HADOOP_CLASSPATH:/opt/module/hbase/lib/*/
    /*过滤搜索也很慢，每条数据都会筛选，性能比较低*/
    /*BinaryComparator,RegexStringComparator 比较器 --》RowFilter里面加上比较器 过滤器过滤rowkey-->scan.setFilter(f)
     OR
     FilterList
     --->table.getScanner(scan)*/
    /*log文件 ==> uuid_appName_timestamp*/
    /*FilterList */


    /**Hbase没有索引概念，但是可以模拟索引，进行查询优化 可以新建一个abc_index表，里面比如经常查询的userName为rowkey
     * abc表插入同名的rowkey时，这个abc_index ,在f列族里面添加一个column=rowkey的列，这样查询时，查询abc_index获取所有的
     * column，然后根据column查询所有对应的abc里面的row。*/


    /*RowKey设计 三原则：唯一 ，散列（），长度原则（64bit系统，8字节的倍数，长度设计为8字节倍数）
    * sha1 散列，或者 timestamp反转字符串，或者日期反转字符串*/

    /**每一个region维护一个startRow和endRow*/

    /**创建预分区*/
    //1.手动
    /*create tableName,columnFamily,SPLITS=>['1000','2000','3000','4000']*/
    /*结果查看Hbase预分区.png*/
//    2.生成16进制序列预分区
    /*create tableName,columnFamily,{NUMREGIONS=> 15, SPLITALGO => 'HexStringSplit'}*/
//生成一个以 11111111,22222222,33333333....99999999....aaaaaaaa...eeeeeeee这14个字符串作为middleKey。
//    3.按照文件里面的row内容作为middleKey进行分区
    /*create tableName,columnFamily,SPLITS_FILE=>'splits.txt'*/
    HashMap map;

    public static void main(String[] args) {
        System.out.println("z".hashCode());
        System.out.println("a".hashCode());
        System.out.println("1".hashCode());
        System.out.println("9".hashCode());

    }
}
