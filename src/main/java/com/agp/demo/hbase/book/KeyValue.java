package com.agp.demo.hbase.book;



import java.io.Serializable;

/**
 * Hbase存储的是KV数据的操作日志
 *
 */
public class KeyValue implements Serializable,Comparable<KeyValue> {
    private byte[] key;
    private byte[] value;
    private OP op;
    private long sequenceId; //自增ID，每个OP PUT DEL操作自增

    @Override
    public int compareTo(KeyValue kv) {
        if (kv==null){
            throw new IllegalArgumentException("kv to compare shouldn't be null.");
        }
//        int compare= Bytes.compare(this.key,kv.key);
//        if(compare!=0) return compare;
//
        return 0;
    }
    //读取的时候只读取小于当前sequenceId的PUT DEL操作，
    //实现了基本的Read Committed的事务隔离级别
}
