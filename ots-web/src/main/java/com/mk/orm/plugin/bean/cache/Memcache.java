//package com.mk.orm.plugin.bean.cache;
//
//import com.mk.orm.plugin.memcached.MemcachedKit;
//
///**
// * Memcache
// *
// */
//public class Memcache implements ICache {
//
//    @Override
//    @SuppressWarnings("unchecked")
//    public <T> T get(String cacheName, Object key) {
//        return (T) MemcachedKit.get(cacheName, key);
//    }
//
//    @Override
//    public void put(String cacheName, Object key, Object value) {
//        MemcachedKit.put(cacheName, key, value);
//    }
//
//    @Override
//    public void remove(String cacheName, Object key) {
//        MemcachedKit.remove(cacheName, key);
//    }
//
//    @Override
//    public void removeAll(String cacheName) {
//        MemcachedKit.removeAll(cacheName);
//    }
//
//}
