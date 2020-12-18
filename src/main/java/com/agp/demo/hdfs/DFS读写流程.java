package com.agp.demo.hdfs;

/**
 * 写流程：
 * 1. 客户端通过调用DistributedFileSystem的create方法创建新文件
 * 2. DistributedFileSystem通过RPC调用namenode去创建一个没有blocks关联的新文件，
 *      创建前，namenode会做各种校验，比如文件是否存在，客户端有无权限去创建等。
 *      如果校验通过，namenode就会记录下新文件，否则就会抛出IO异常.
 * （3）NameNode通过执行不同的检查来确保这个文件不存在而且客户端有新建该文件的权限。
 *  如果这些检查都通过了，NameNode就会为创建新文件写下一条记录；
 *  反之，如果文件创建失败，则向客户端抛出一个IOException异常。
 *（4）随后DistributedFileSystem向客户端返回一个FSDataOutputStream对象，
 *      这样客户端就可以写入数据了。和读取事件类似，FSDataOutputStream封装一个DFSOutputStream对象，
 *      该对象会负责处理DataNode和NameNode之间的通信。
 *      在客户端写入数据的时候，DFSOutputStream将它分成一个个的数据包，
 *      并且写入内部队列，被称之为“数据队列”（data queue）。
 * （5）DataStream处理数据队列，它的任务是选出适合用来存储数据副本的一组DataNode，
 *      并据此要求NameNode分配新的数据块。这一组DataNode会构成一条管线，
 *      DataStream会将数据包流式传输到管线中的第一个DataNode
 *      ，然后依次存储并发送给下一个DataNode。
（6）DFSOutPutStream也维护着一个内部数据包队列来等待DataNode的收到确认回执，
 *      称为“确认队列”（ask queue）。
 *      收到管道中所有DataNode确认信息后，该数据包才会从确认队列删除。
 *
 * 7）客户端完成数据的写入后，会对数据流调用close()方法，如图3.6所示。
 */
public class DFS读写流程 {

    /*
    * 读取流程：
    *
    * HDFS读文件流程
    *
    * （1）客户端通过调用FileSystem对象的open()方法打开要读取的文件，
    *   对于HDFS来说，这个对象是DistributedFileSystem的一个实例。
    * （2）DistributedFileSystem通过使用远程过程调用（RPC）来调用NameNode，以确定文件起始块的位置。
    * （3）对于每个块，NameNode返回到存有该块副本的DataNode地址。
    * 此外，这些DataNode根据它们与客户端的距离来排序。
    * 如果该客户端本身就是一个DataNode，
    * 那么该客户端将会从包含有相应数据块副本的本地DataNode读取数据。
    * DistributedFileSystem类返回一个FSDataInputStream对象给客户端并读取数据，
    * FSDataInputStream转而封装DFSInputStream对象，
    * 该对象管理着DataNode和NameNode的I/O。
    *   接着，客户端对这个输入流调用read()方法。
    * （4）存储着文件起始几个块的DataNode地址的DFSInputStream，
    * 接着会连接距离最近的文件中第一个块所在的DataNode。
    * 通过对数据流的反复调用read()方法，实现将数据从DataNode传输到客户端。
    *
    * （5）当快到达块的末端时，DFSInputStream会关闭与该DataNode的连接，
    * 然后寻找下一个块最佳的DataNode。
    *
    * （6）当客户端从流中读取数据时，
    *   块是按照打开的DFSInputStream与DataNode新建连接的顺序进行读取的。
    *   它也会根据需要询问NameNode从而检索下一批数据块的DataNode的位置。
    *   一旦客户端完成读取，
    *   就对FSDataInputStream调用close()方法，如图3.5所示。
    *
    * */
}
