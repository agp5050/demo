package com.agp.demo.flink;

/**
 * * A {@code Trigger} determines when a pane of a window
 * should be evaluated to emit the
 *  * results for that part of the window.
 *  //相同key的要结束时会发射这个trigger。
 *
 *A pane is the bucket of elements that have the
 *  same key (assigned by theKeySelector)
 *
 * An element can
 *  * be in multiple panes if it was assigned to multiple windows by the
 *WindowAssigner
 *These panes all
 *  * have their own instance of the {@code Trigger}.
 *
 *Triggers must not maintain state internally since they can be re-created or reused for
 *  * different keys.
 *  一个window里面不同的key有不同的trigger
 *
 *All necessary state should be persisted using the state abstraction
 *  * available on the TriggerContext.
 *
 *
 *  实现类：
 *  CountTrigger  EventTimeTrigger ProcessingTimeTrigger  PurgingTrigger
 *
 *
 *
 *
 *
 *
 *
 *
 */
public class TriggerAnnotation {
}
