package com.agp.demo.hbase.book;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;

public interface MiniBase extends Closeable {
    void put(byte[] key,byte[] value) throws IOException;
    byte[] get(byte[] key) throws IOException;
    void delete(byte[] key) throws IOException;
    interface Iter<KeyValue>{
        boolean hasNext() throws IOException;
        KeyValue next() throws IOException;
    }

    <KeyValue> Iter<KeyValue> scan(byte[] startKey,byte[] stopKey) throws IOException;
}
