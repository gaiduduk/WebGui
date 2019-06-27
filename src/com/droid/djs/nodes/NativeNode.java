package com.droid.djs.nodes;

import com.droid.djs.consts.LinkType;
import com.droid.djs.consts.NodeType;
import com.droid.djs.runner.Func;
import com.droid.djs.runner.utils.Utils;

public class NativeNode extends Node {

    private Integer functionIndex; // TODO change to functionIndex
    public Func func;

    public NativeNode() {
        super(NodeType.NATIVE_FUNCTION);
    }

    @Override
    public void listLinks(NodeLinkListener linkListener) {
        super.listLinks(linkListener);
        if (functionIndex != null)
            linkListener.get(LinkType.NATIVE_FUNCTION_NUMBER, (long) (int) functionIndex, true);
    }

    public void setFunctionIndex(Integer functionIndex) {
        this.functionIndex = functionIndex;
        func = Utils.functions.get(functionIndex);
    }

    @Override
    void restore(LinkType linkType, long linkData) {
        super.restore(linkType, linkData);
        if (linkType == LinkType.NATIVE_FUNCTION_NUMBER) {
            setFunctionIndex((int) linkData);
        }
    }
}
