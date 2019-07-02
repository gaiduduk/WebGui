package com.droid.djs;

import com.droid.djs.consts.NodeType;
import com.droid.djs.fs.Master;
import com.droid.djs.nodes.DataInputStream;
import com.droid.djs.nodes.NativeNode;
import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.ThreadNode;
import com.droid.djs.nodes.data.BoolData;
import com.droid.djs.nodes.data.Data;
import com.droid.djs.nodes.data.NumberData;
import com.droid.djs.nodes.data.StringData;
import com.droid.djs.runner.utils.Utils;
import com.droid.gdb.*;

import java.util.*;

public class NodeStorage extends InfinityStringArray {

    private static NodeStorage instance;

    private static final String nodeStorageID = "node";

    private static final int MAX_TRANSACTION_CACHE_NODE_COUNT = 10;
    private static ArrayList<Node> transactionNodes;
    private static Map<Long, Node> nodesCache;


    public NodeStorage(String infinityFileID) {
        super(infinityFileID);
    }

    private void initStorage() {
        ThreadNode root = new ThreadNode();
        add(root);
        transactionCommit();
        Master.getInstance();
        Utils.getFunctions();
    }

    public static NodeStorage getInstance() {
        if (instance == null) {
            nodesCache = new TreeMap<>();
            transactionNodes = new ArrayList<>();
            instance = new NodeStorage(nodeStorageID);
            if (instance.fileData.sumFilesSize == 0)
                instance.initStorage();
        }
        return instance;
    }

    public void addToTransaction(Node node) {
        if (transactionNodes.size() >= MAX_TRANSACTION_CACHE_NODE_COUNT)
            transactionCommit();
        transactionNodes.add(node);
        node.isSaved = true;
        nodesCache.put(node.id, node);
    }

    public void transactionCommit() {
        // TODO change transactionNodes to sync list
        synchronized (transactionNodes) {
            for (Node commitNode : transactionNodes) {
                if (commitNode.id == null)
                    add(commitNode);
                else
                    set(commitNode.id, commitNode);
                commitNode.isSaved = false;
            }
            transactionNodes.clear();
        }
    }

    @Override
    public MetaCell initMeta() {
        return new NodeMetaCell();
    }

    public Node newNode(NodeType nodeType) {
        switch (nodeType) {
            case BOOL:
                return new BoolData();
            case NUMBER:
                return new NumberData();
            case STRING:
                return new StringData();
            case NATIVE_FUNCTION:
                return new NativeNode();
            case THREAD:
                return new ThreadNode();
            default:
                return new Node(nodeType);
        }
    }

    public Node get(Long index) {
        Node node = nodesCache.get(index);
        if (node == null) {
            NodeMetaCell metaCell = (NodeMetaCell) getMeta(index);
            NodeType nodeType = NodeType.values()[metaCell.type];
            node = newNode(nodeType);
            node.id = index;
            node.type = nodeType;
            if (nodeType.ordinal() < NodeType.NODE.ordinal()) {
                ((Data) node).data = new DataInputStream(nodeType, metaCell.start, metaCell.length);
            } else {
                byte[] readiedData = read(metaCell.start, metaCell.length);
                if (readiedData == null)
                    return null;
                node.parse(readiedData);
            }
            nodesCache.put(index, node);
        }
        return node;
    }

    public void set(long index, Node node) {
        if (node.type.ordinal() >= NodeType.NODE.ordinal())
            super.setObject(index, node);
        // else {data is not mutable}
    }


    public void add(Node node) {
        byte[] data = node.build();
        NodeMetaCell metaCell = new NodeMetaCell();
        if (data != null/* && data.length != 0*/) {
            byte[] sector = dataToSector(data);
            metaCell.type = (byte) node.type.ordinal();
            metaCell.start = super.add(sector);
            metaCell.length = data.length;
        }
        node.id = meta.add(metaCell);
    }
}
