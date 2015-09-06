package com.mk.orm.plugin.memcached;

import com.mk.orm.plugin.IPlugin;

public class MemcachedPlugin implements IPlugin {
    
    public MemcachedPlugin() {
        
    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public boolean stop() {
        return false;
    }

}
