//package com.mk.orm.plugin.memcached;
//
//import com.mk.orm.log.Logger;
//import com.mk.ots.domain.IBean;
//
//
//
//public class MemcachedKit {
//    private static volatile MemcachedManager cacheManager;
//    private static final Logger log = Logger.getLogger(MemcachedKit.class);
//    
//    /**
//     * 
//     * @param cacheManager
//     */
//    static void init(MemcachedManager cacheManager) {
//        MemcachedKit.cacheManager = cacheManager;
//    }
//    
//    /**
//     * 
//     * @return
//     */
//    public static MemcachedManager getCacheManager() {
//        return cacheManager;
//    }
//    
//    /**
//     * 
//     * @param cacheName
//     * @return
//     */
//    static IBean getOrAddCache(String cacheName) {
//        IBean cache = cacheManager.getCache(cacheName);
//        if (cache == null) {
//            synchronized(cacheManager) {
//                cache = cacheManager.getCache(cacheName);
//                if (cache == null) {
//                    log.warn("Could not find cache config [" + cacheName + "], using default.");
//                    cacheManager.addCacheIfAbsent(cacheName);
//                    cache = cacheManager.getCache(cacheName);
//                    log.debug("Cache [" + cacheName + "] started.");
//                }
//            }
//        }
//        return cache;
//    }
//    
//    /**
//     * 
//     * @param cacheName
//     * @param key
//     * @return
//     */
//    @SuppressWarnings("unchecked")
//    public static <T> T get(String cacheName, Object key) {
//        IBean bean = (IBean) getOrAddCache(cacheName);
//        return bean != null ? (T)bean.get((String)key) : null;
//    }
//    
//    /**
//     * 
//     * @param cacheName
//     * @param key
//     * @param value
//     */
//    public static void put(String cacheName, Object key, Object value) {
//        getOrAddCache(cacheName).set((String)key, value);
//    }
//    
//    /**
//     * 
//     * @param cacheName
//     * @param key
//     */
//    public static void remove(String cacheName, Object key) {
//        getOrAddCache(cacheName).remove((String)key);
//    }
//    
//    /**
//     * 
//     * @param cacheName
//     */
//    public static void removeAll(String cacheName) {
//        getOrAddCache(cacheName).clear();
//    }
//}
