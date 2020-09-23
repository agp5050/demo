package com.agp.demo.hive;

public class HiveAnnotation {
    /**hive version 1.2.1
     *hive 默认数据库derby。 hive客户端只能一个连接。要修改为使用mysql存储元数据。
     *默认metastore存储在derby，改为mysql
     *
     * 如果linux默认按照mysql版本不合适，需要卸载：rpm -e --nodeps mysql-libs-****
     *
     * 安装mysql-server mysql-devel
     * ALTER USER 'root'@'localhost' IDENTIFIED BY 'new password';
     * update user set host='%' where user='root'
     * flush privileges;
     *
     * */

   /*
   bin/hiveserver2  -->启动一个服务，供jdbc连接 这个服务器，进而操作hive里面的mysql和hadoop。
    */
   /*
   * bin/beeline ->本地连接hive服务。
   *
   * */
   /*连接hiveserver2    第一步 beeline 然后执行 !connect jdbc:hive2://node1:10000*/

    /**bin/hive 直接本地操作hive的mysql库，不经过hive本身启动的服务器。*/

    /* load data local inpath 'aaaa' into table abc 这个不走MR任务 所以分桶时不能用这个*/
    /*insert overwrite local directory 'aasdfasdf' select * from abc order by dept*/

    /*insert overwrite directory 'ssasdf' select *from abc distribute by deptno sort by sal 先分区然后排序
    * 按照指定的distribute字段进行hash。*/
    /**sort by && order by  --》 order by确定整体的顺序。 sort by，如果不是一个reducer，只能确保各个reducer内部的order*/
    /*cluster by == distribute by deptno sort by deptno 比如只有3个reducer，100个部门，每个reducer里面有33个，要排序。*/

    /*insert into/overwrite abc select * from bcd*/
//分区
    /** partitioned by ==> create table abc(name string,age int,level int) partitioned by (level int) row format delimited
     * fields terminated by '\t'*/


    /*from student insert overwrite table student partition(month='20200916') select * where month ='20200915'*/

    /*创建外表时，hive删除表，数据还在hdfs。 如果再恢复这个表可以使用指定location即可*/
    /**create table if not exists student2(name string,age int) row format delimited fields terminated by '\t'
     * location '/user/hive/warehouse/student'*/
//    然后select * from student2即可看到表的数据了。
    /*show partitions student*/

    /**alter table student add partition(level='5');*/

    /**动态分区 bin/hive > hive.exec.dynamic.partition=true
     * set hive.exec.dynamic.partition.mode=nonstrict
     * 然后执行 load data || insert into\overwrite*/

    /*分区针对的是数据的存储路径*/

    /*分桶 针对的是数据文件，将文件分成多个*/
    /**create table student(id int,name string,level int) clustered by(level int)
     * into 4 buckets
     * row format delimited fields terminated by '\t'*/
    /*4个桶代表MR任务4个Reducer，cluster by代表使用level 作为partition的key*/
    /*需要通过MR任务才能将数据导入分桶，所以不能load data，只能 insert into\overwrite*/



}
