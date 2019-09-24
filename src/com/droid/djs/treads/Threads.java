package com.droid.djs.treads;

import com.droid.djs.store_models.nodes.NodeBuilder;
import com.droid.djs.consts.NodeType;
import com.droid.djs.store_models.nodes.Node;
import com.droid.djs.store_models.nodes.ThreadNode;
import com.droid.instance.Instance;


import java.util.ArrayList;
import java.util.List;

public class Threads {

    private List<Thread> threadList = new ArrayList<>();

    private ThreadNode findThread(Node node) {
        NodeBuilder builder = new NodeBuilder();
        Node item = node;
        while (item.type != NodeType.THREAD)
            item = builder.set(item).getLocalParentNode();
        return (ThreadNode) item;
    }

    public boolean run(Node node, Node[] args, boolean async, Long accessToken) {
        return findThread(node).run(node, args, async, accessToken);
    }

    public boolean run(Node node, Node[] args, boolean async) {
        return findThread(node).run(node, args, async, Instance.get().accessToken);
    }

    public boolean run(Node node, Node[] args) {
        return findThread(node).run(node, args, false, Instance.get().accessToken);
    }

    public boolean run(Node node) {
        return findThread(node).run(node, null, false, Instance.get().accessToken);
    }

    public void registration(Thread thread) {
        threadList.add(thread);
    }

    public void stopAllThreads() {
        for (Thread thread : threadList)
            if (thread.isAlive())
                thread.stop();
    }
}
