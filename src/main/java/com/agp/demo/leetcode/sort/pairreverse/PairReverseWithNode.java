package com.agp.demo.leetcode.sort.pairreverse;

import com.agp.demo.annotation.WrongInvoke;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 *  1->2->3->4->5->6->7  pairReverse 为 2->1->4->3->6->5->7
 */
public class PairReverseWithNode {
   class Node{
       private int value;
       private Node next;
       public Node(){}
       public Node(int v){
           this.value=v;
       }

       public int getValue() {
           return value;
       }

       public void setValue(int value) {
           this.value = value;
       }

       public Node getNext() {
           return next;
       }

       public void setNext(Node next) {
           this.next = next;
       }

       public  Node getNode(List<Integer> list){
           Node first=new Node();
           Node father=first;
           if (CollectionUtils.isEmpty(list)) return first;
           int size = list.size();
           for (int i=0;i<size;i++){
               if (i==0){
                   first.setValue(list.get(i));
               }else {
                   Node newNode = new Node(list.get(i));
                   father.setNext(newNode);
                   father=newNode;
               }
           }
           return first;
       }

       @Override
       public String toString() {
          StringBuilder sb=new StringBuilder("Node:");
          sb.append(this.value);
          Node first=this;
          while (first.next!=null){
              sb.append("->")
                      .append(first.next.value);
              first=first.next;
          }
          return sb.toString();
       }
   }

   public static Node pairSwap(Node node){
       if (node==null || node.next==null) return node;
       Node first=node;
       Node second=node.next;
       first.next=pairSwap(second.next);  // node.next.next 在括号或者左边 代表着这个指针指向的值。简称代表实际元素
       second.next=first;  //second.next 在等号右边代表指针。  //不能放到上面， 会导致 second.next =first  ;first.next=pairSwap(first);
       return second;
   }
    @WrongInvoke("//死循环 tmpSecond.next == second.next==first 。并没有给second赋值，没有覆盖second。 ")
   public static Node pairSwap2(Node node){
       if (node==null || node.next==null) return node;
       Node first=node;
       Node second=node.next;
       Node tmpSecond=second;
       second.next=first;
       first.next=pairSwap2(tmpSecond.next);  //死循环 tmpSecond.next == second.next==first
       return second;
   }

   @Test
    public void test(){
       Node node = new Node().getNode(Arrays.asList(1, 2, 3, 4, 5, 6,7));
       System.out.println(node);
//       Node node1 = pairSwap(node);
       Node node2 = pairSwap2(node);
//       System.out.println(node1);
       System.out.println(node2);
   }

}
