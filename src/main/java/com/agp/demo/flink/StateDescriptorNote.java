package com.agp.demo.flink;

/**
 * StateDescriptor is Base class for state descriptors
 *is used for creating partitioned State in stateful operations
 *
 *Subclasses must correctly implement equals(Object) and hashCode()
 *  分为如下几类Descriptor
 *
 *      VALUE,
 * 		LIST,
 * 		REDUCING,
 * 		FOLDING,
 * 		AGGREGATING,
 * 		MAP
 *
 *
 *RuntimeContext.getListState():ListState
 *RuntimeContext.getState():ValueState
 *RuntimeContext.getMapState:MapState
 *getAggregatingState
 *getReducingState
 *
 *
 * ListState：Interface  extends AppendingState
 * AppendingState 里面有add和get方法，拿到对应的值
 *
 *
 *
 */
public class StateDescriptorNote {
}
