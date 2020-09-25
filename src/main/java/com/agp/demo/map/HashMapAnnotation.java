package com.agp.demo.map;

import java.util.HashMap;

/**
 * HashMap优化1》(h = key.hashCode()) ^ (h >>> 16)
 * 高16位和低16位反应。 由于Hash使用MasK 1111111这样快速计算出
 * 落点位置。  高位+地位 让 2^16limit下的hashmap更散列化。
 *
 * PutTreeVal()方法：
 * ####通过root节点的next可以遍历所有的子节点。
 * 先摘除掉p的next。 这个next放为新建节点的next。 然后p的next设置为新建节点。
 *
 * P有左右LR两个子节点，这两个节点按照创建时间倒排。   往上溯源也是每个节点的两个子节点也是next 或者 next.next里面的的一员。
 *
 *
 *           TreeNode<K,V> xp = p;
 *        if ((p = (dir <= 0) ? p.left : p.right) == null) {
 *             HashMap.Node<K,V> xpn = xp.next;
 *             HashMap.TreeNode<K,V> x = map.newTreeNode(h, k, v, xpn);
 *             if (dir <= 0)
 *                 xp.left = x;
 *             else
 *                 xp.right = x;
 *             xp.next = x;
 *             x.parent = x.prev = xp;
 *             if (xpn != null)
 *                 ((HashMap.TreeNode<K,V>)xpn).prev = x;
 *             moveRootToFront(tab, balanceInsertion(root, x));
 *             return null;
 *         }
 *
 *
 *split（）方法，扩容时可以将当前bucket里面的split为高低两个链表。
 * (e.hash & bit) == 0  bit为2^n次幂，不是mask1111这种。 但凡==0 说明
 * 1000    为 小于8 或者  是8的偶数被 即不是 11000这种奇数倍。
 * 偶数倍的直接扩容<<1时，正好能new hash抵消。所以 lohead,hihead这两个单向列表
 * 一个可以就在当前位置不变。一个需要在 index+bit位置。 不会散落到其他位置！！！！
 *
 *

 */
public class HashMapAnnotation {
/* *        if (++size > threshold)
 *             resize()  -->balanceInsertion 当插入节点时treenode时即>8个当前bucket*/
/**map中元素的个数size》threshold时即大于capacity*0.75时进行*/


/*TreeNode.removeTreeNode--》balanceDeletion 当删除节点时treenode且》8个。删除后再平衡红黑树*/

}
