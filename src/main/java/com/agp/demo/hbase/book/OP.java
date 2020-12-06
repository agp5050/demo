package com.agp.demo.hbase.book;

public enum OP {
    PUT((byte)0),
    DELETE((byte)1);
    private byte code;
    private OP(byte code){
        this.code=code;
    }
}
