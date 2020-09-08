package com.agp.demo.leetcode.tree;

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
        System.out.println(list);
    }
}
