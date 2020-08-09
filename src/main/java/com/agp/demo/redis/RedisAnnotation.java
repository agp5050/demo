package com.agp.demo.redis;

/**
 * Redis cluster:
 *         connection = connectionHandler.getConnectionFromSlot(JedisClusterCRC16.getSlot(key));
 *         对每个Key会CRC16计算对应的1<<14 个槽16384，的哪个槽里面。
 *  有一个对应的槽ID和JedisPool的Map
 *  JedisClusterInfoCache --->private final Map<Integer, JedisPool> slots = new HashMap<Integer, JedisPool>();
 */

/*
* 会从redis里面获取对应的slots信息。 并注册到各个
*   private void discoverClusterSlots(Jedis jedis) {
    List<Object> slots = jedis.clusterSlots();
    this.slots.clear();

    for (Object slotInfoObj : slots) {
      List<Object> slotInfo = (List<Object>) slotInfoObj;

      if (slotInfo.size() <= MASTER_NODE_INDEX) {
        continue;
      }

      List<Integer> slotNums = getAssignedSlotArray(slotInfo);

      // hostInfos
      List<Object> hostInfos = (List<Object>) slotInfo.get(MASTER_NODE_INDEX);
      if (hostInfos.isEmpty()) {
        continue;
      }

      // at this time, we just use master, discard slave information
      HostAndPort targetNode = generateHostAndPort(hostInfos);
      assignSlotsToNode(slotNums, targetNode);
    }
  }
*
* */
public class RedisAnnotation {
    public static void main(String[] args) {
    }
}
