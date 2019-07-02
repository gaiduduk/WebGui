package com.droid.net.ftp;

import com.droid.djs.fs.Branch;
import com.droid.djs.serialization.js.JsParser;
import com.droid.djs.nodes.Node;
import com.droid.djs.builder.NodeBuilder;
import com.droid.djs.fs.Files;
import com.droid.djs.serialization.json.JsonParser;

import java.io.*;
import java.util.Random;

public class DataOutputStream extends OutputStream {

    public final static File ftpTempDir = new File("out/FtpTemp");
    public final static Random random = new Random();
    private Node node;
    private File tempFile = new File(ftpTempDir, "" + random.nextInt());
    private FileOutputStream out;
    private Branch branch;

    public DataOutputStream(Branch branch, Node node) {
        this.branch = branch;
        this.node = node;
        if (!ftpTempDir.exists())
            ftpTempDir.mkdirs();
        try {
            out = new FileOutputStream(tempFile);
        } catch (Exception e1) {
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        out.write(b);
        branch.updateTimer();
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    @Override
    public void close() throws IOException {
        out.close();
        // TODO error with uploading a empty file
        Node res = Files.putFile(node, new FileInputStream(tempFile));
        NodeBuilder builder = new NodeBuilder();
        String newFileName = builder.set(res).getTitleString().toLowerCase();
        if (newFileName.endsWith(".json")) {
            new JsonParser().parse(res);
        } else if (newFileName.endsWith(".node.js")) {
            NodeBuilder value = new NodeBuilder().set(builder.getValueNode());
            String sourceCode = value.getData().readString();
            builder.setValue(null).commit();
            new JsParser().parse(res, sourceCode);
        } else if (newFileName.equals("index.html")) {
            Node parent = builder.set(node).getLocalParentNode();
            parent.parse(node.build());
            builder.set(parent).commit();
            // TODO delete local from parent
        }
        tempFile.delete();
    }
}
