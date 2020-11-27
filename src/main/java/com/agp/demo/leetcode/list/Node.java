package com.agp.demo.leetcode.list;

import java.util.Arrays;

public class Node<T extends Comparable<T>> {
    public Node next;
    public T value;
    public Node(){}
    public Node(T value){
        this.value=value;
    }
    public Node(T ... values){
        Arrays.sort(values);
        Node n,b=null;
        for (T v:values){
            if (value ==null){
                value=v;
                n=next;
                b=this;
            }else {
                n=new Node(v);
                b.next=n;
                b=n;
                n=n.next;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder("Node:"+this.value);
        Node next=this.next;
        while (next!=null){
            sb.append("->"+next.value);
            next=next.next;
        }
        return sb.toString();
    }

    public static class NodeUtils{
        public static   Node combineListsAsc(Node first,Node second){
           int compare=0;
           Node result=null,node=null;
           Comparable tmp;
           Node b1=first;
           Node b2=second;
           while (true){
               if (b1==null && b2==null) return result;
               if (b1!=null && b2!=null){
                   compare=b1.value.compareTo(b2.value);
                   if (compare<0){
                      tmp=  b1.value;
                      b1=b1.next;
                   }else {
                       tmp=  b2.value;
                       b2=b2.next;
                   }
                   if (result==null){
                       result=new Node(tmp);
                       node=result;
                   }else {
                       node.next=new Node(tmp);
                       node=node.next;
                   }
               }
               else if (b1!=null){
                   tmp=b1.value;
                   b1=b1.next;
                   node.next=new Node(tmp);
                   node=node.next;
               }
               else if (b2!=null){
                   tmp=b2.value;
                   b2=b2.next;
                   node.next=new Node(tmp);
                   node=node.next;
               }


           }

        }
    }
}
