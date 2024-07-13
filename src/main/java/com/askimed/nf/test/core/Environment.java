package com.askimed.nf.test.core;

import com.askimed.nf.test.plugins.PluginManager;

public class Environment {

    private String libDir = "";

    private PluginManager pluginManager = null;

    public void setLibDir(String libDir) {
        this.libDir = libDir;
    }

    public String getLibDir() {
        return libDir;
    }

    public void setPluginManager(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }
}
