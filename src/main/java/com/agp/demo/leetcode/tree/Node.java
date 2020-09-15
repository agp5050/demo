package com.agp.demo.leetcode.tree;

import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Node<T> {
    T data;
    Node left;
    Node right;
    public Node(T data){
        this.data=data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public static <T> Node getInstance(T[] ary){
        Node<T>[] nodes=new Node[ary.length];
        for (int i=0;i<ary.length;i++){
            if (ary[i]==null) continue;
            nodes[i]=new Node<>(ary[i]);
        }
        for (int i=0;i<nodes.length;i++){
            if (nodes[i]==null)continue;
            if (i*2+1<nodes.length && nodes[i*2+1]!=null){
                nodes[i].setLeft(nodes[i*2+1]);
            }
            if (i*2+2<nodes.length && nodes[i*2+2]!=null){
                nodes[i].setRight(nodes[i*2+2]);
            }
        }
        return nodes[0];
    }


    public static <T> Node getInstanceWithNull(T[] ary){
        int null_num=0;//每遇到一个null添加偏移量。
        Node<T>[] nodes=new Node[ary.length];
        for (int i=0;i<ary.length;i++){
            if (ary[i]==null) continue;
            nodes[i]=new Node<>(ary[i]);
        }
        for (int i=1;i<nodes.length;i++){
            if (nodes[i]==null)continue;
            int fatherNodeIndex = getFatherNodeIndex(i);
            while (fatherNodeIndex+null_num< nodes.length && nodes[fatherNodeIndex+null_num]==null){
                null_num++;
            }
            if (fatherNodeIndex+null_num< nodes.length){
                if (i%2!=0) nodes[fatherNodeIndex+null_num].left=nodes[i];
                else nodes[fatherNodeIndex+null_num].right=nodes[i];
            }

        }
        return nodes[0];
    }

    private static int getFatherNodeIndex(int i) {
        if (i%2==0) return i/2-1 ;
        else return i/2;

    }


    public static <T> List<T> middleTraverse(Node<T> node){
        List<T> rst=new ArrayList<>();
        recursiveTraverse(node,rst);
        return rst;
    }

    private static <T> void recursiveTraverse(Node<T> node, List<T> rst) {
        if (node ==null) return;
        if (node.left!=null) recursiveTraverse(node.left,rst);
         rst.add(node.data); //不能做 if(node.left==null) rst.add这样的话只能加叶子节点。非叶子节点就加不上了。
        if (node.right!=null) {
            recursiveTraverse(node.right,rst);
        }
    }


    //采用非递归中序遍历二叉树
    public static void printTree(Node root){
        Stack<Node> stack=new Stack<Node>();
        List<Node> list=new ArrayList<Node>();
        Node node=root;
        //刚开始先把根节点压入栈，往后每次判断节点不为空则压栈
        do {
            while(node!=null){
                stack.push(node);
                node=node.left;
            }
            node=stack.pop();
            list.add(node);
            //如果出栈的结点存在右节点，则指向该节点
            if(node.right!=null){
                node=node.right;
            }else{//否则设置为空，下次循环继续出栈
                node=null;
            }//当栈不为空，或者当前节点引用不为空时循环
        } while (!stack.isEmpty()||node!=null);
        List rst2=new ArrayList(list.size());
        for (Node nod:list){
            rst2.add(nod.data);
        }
        System.out.println(JSONArray.toJSONString(rst2));
    }
}
