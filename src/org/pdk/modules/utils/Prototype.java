package org.pdk.modules.utils;

public abstract class Prototype extends Module {

    public static String DEFAULT_PROTOTYPES_DIR = "Prototype/";

    public abstract String name();

    @Override
    public String path() {
        return DEFAULT_PROTOTYPES_DIR + name();
    }

    @Override
    public void methods() {

    }
}
