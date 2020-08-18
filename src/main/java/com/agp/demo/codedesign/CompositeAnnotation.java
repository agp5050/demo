package com.agp.demo.codedesign;

import java.util.ArrayList;
import java.util.List;

/**
 * 组合模式，专门处理树状结构的
 *
 */
public class CompositeAnnotation {
    public static void main(String[] args) {
        BranchNode root=new BranchNode("root");
        BranchNode l1=new BranchNode("l1");
        LeafNode l11=new LeafNode("XX","l11");
        LeafNode l12=new LeafNode("FF","l12");
        BranchNode l2=new BranchNode("l2");
        BranchNode l21=new BranchNode("l21");
        LeafNode l211=new LeafNode("DD","l211");
        LeafNode l212=new LeafNode("EE","l212");
        l1.addNode(l11).addNode(l12);
        l21.addNode(l211).addNode(l212);
        l2.addNode(l21);
        root.addNode(l1).addNode(l2);
        tree(root,0);
    }

    private static void tree(Node node,int dep) {
        for (int i=0;i<=dep;++i) System.out.print("--");
        if (node instanceof BranchNode){
            System.out.println(node.name);
            ++dep;
            for (Node item: ((BranchNode)node).subNodes){
                tree(item,dep);
            }
        }else if (node instanceof LeafNode){
            LeafNode node1 = (LeafNode) node;
            System.out.println(node1.name+" : "+node1.content);
        }

    }
}

class Node{
    String name;
    public Node(String name){
        this.name=name;
    }
}
class LeafNode extends Node{
    String content;
    public LeafNode(String content,String name){
        super(name);
        this.content=content;
    }

}
class BranchNode extends Node{
    List<Node> subNodes=new ArrayList<>();
    public BranchNode(String name) {
        super(name);
    }
    public BranchNode addNode(Node node){
        subNodes.add(node);
        return this;
    }
}