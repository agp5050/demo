package com.agp.demo.leetcode.tree;

import java.util.List;

public class MiddleOrderTraverse {
    public static void main(String[] args) {
        Node instance = Node.getInstanceWithNull(new Integer[]{1, 4, null, null,5,6,7});
        List<Integer> list = Node.middleTraverse(instance);
        System.out.println(list);
        Node.printTree(instance);


    }
}
