package com.agp.demo.leetcode.list;

public class NodeTest {
    public static void main(String[] args) {
        Node node = new Node("1", "3", "2", "4", "1", "5");
        System.out.println(node);
        Node node1 = new Node("1", "5", "1", "2", "6", "7");
        System.out.println(node1);
        Node result = Node.NodeUtils.combineListsAsc(node, node1);
        System.out.println("result: "+result);

        Node<Integer> integerNode = new Node<>(1, 2, 3, 4, 5, 1);
        System.out.println(integerNode);
        Node<Integer> integerNode1 = new Node<>(2, 3, 1, 7, 4);
        System.out.println(integerNode1);
        System.out.println(Node.NodeUtils.combineListsAsc(integerNode,integerNode1));
    }
}
