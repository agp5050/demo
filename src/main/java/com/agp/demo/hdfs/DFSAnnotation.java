package com.agp.demo.hdfs;

/**
 * All blocks in a file except the last block are the same size,
 * while users can start a new block without filling out the last block
 * to the configured block size after the support
 * for variable length block was added to append and hsync.
 *
 * An application can specify the number of replicas of a file.
 * The replication factor can be specified at file creation time
 * and can be changed later.
 * Files in HDFS are write-once (except for appends and truncates)
 * and have strictly one writer at any time.
 *
 *
 * 心跳(3 seconds)和 DataNode（1小时定时report）
 * The NameNode makes all decisions regarding replication of blocks.
 * It periodically receives a Heartbeat and a Blockreport
 * from each of the DataNodes in the cluster.
 * Receipt of a Heartbeat implies that the DataNode is functioning properly.
 * A Blockreport contains a list of all blocks on a DataNode.
 *
 *
 * .
 */
public class DFSAnnotation {
}
