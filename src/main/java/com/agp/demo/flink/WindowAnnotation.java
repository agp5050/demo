package com.agp.demo.flink;

/**WindowedStream
 *A WindowedStream represents a data stream where elements are grouped by
 *  * key
 *, and for each key, the stream of elements is split into windows based on a
 *WindowAssigner   每一个key消息可以被切到不同的window里面，基于Window指派器
 *
 * Window emission
 *  * is triggered based on a  Trigger
 *窗口们为每一个key 概念上来说都执行一次计算。 意味着窗口们对每一个key，可以在不同的点上面进行触发
 * （因为同key可以分配到几个窗口（滑动窗口）。
 *The windows are conceptually evaluated for each key individually,meaning windows can trigger
 *at different points for each key.
 *  为每一个key消息都执行一次
 * The window function is called for each valuation of the window for each key individually.
 *
 * If an Evictor（驱逐器）is specified it will be used to evict elements from the window after
 *evaluation was triggered by the Trigger but before the actual evaluation of the window
 *
 *
 *When using an evictor window performance will degrade significantly since
 *incremental aggregation of window results cannot be used.
 *
 * 窗口里面有维护的状态，相同的key用相同的状态 aggregate
 *
 */
public class WindowAnnotation {
}
