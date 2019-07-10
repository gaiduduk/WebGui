package com.droid.djs.builder;

import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.NodeBuilder;

import static org.junit.jupiter.api.Assertions.*;

class NodeBuilderTest {

    static void setValue(){
        NodeBuilder builder = new NodeBuilder();
        Node node =  builder.create().commit();
        Node nodeId = builder.create().setValue(node).commit();
        Long valueId = builder.set(nodeId).getValue();
        assertEquals(node.id, valueId);
    }

    public static void main(String[] args) {
        System.out.println(1);
        setValue();
    }

}