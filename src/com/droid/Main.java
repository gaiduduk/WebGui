package com.droid;

import com.droid.djs.fs.Branch;
import com.droid.djs.fs.Files;
import com.droid.djs.treads.Secure;

import com.droid.net.ftp.DataOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Main {

    public static final String login = "john";
    public static String password = "123";

    private static Branch loadingBranch = new Branch();

    public static void main(String[] args) throws InterruptedException {
        loadProject("C:/wamp/www/droid", "");
        loadingBranch.mergeWithMaster();
        Secure.start(login, password);
        Secure.join();
    }

    private static void loadProject(String projectPath, String localPath) {
        File root = new File(projectPath + localPath);
        File[] list = root.listFiles();
        if (list == null) return;
        for (File file : list) {
            String localFileName = localPath + "/" + file.getName();
            if (file.isDirectory()) {
                loadProject(file.getAbsolutePath(), localFileName);
            } else {
                try {
                    DataOutputStream dataOutputStream = new DataOutputStream(loadingBranch, Files.getNode(loadingBranch.getRoot(), localFileName));
                    FileInputStream fileInputStream = new FileInputStream(file);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fileInputStream.read(buffer)) != -1)
                        dataOutputStream.write(buffer, 0, len);
                } catch (IOException ignored) {
                }
            }
        }
    }
}
