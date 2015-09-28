/**
 * Copyright (c) 2014-2015, IT Group of MK.
 */
package com.mk.orm.plugin.memcached;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.mk.ots.domain.IBean;
import com.mk.ots.domain.MBean;

/**
 * MemcachedManager
 * @author chuaiqing.
 *
 */
public class MemcachedManager {
//    // 创建全局的MemCachedClient对象唯一实例
//    protected static MemCachedClient memcachedClient = new MemCachedClient();
//    // 创建单例MemcachedManage对象实例
//    protected static MemcachedManager memcachedManager = new MemcachedManager();
//    
//    private final ConcurrentMap<String, IBean> caches = new ConcurrentHashMap<String, IBean>();
//    
//    private IBean defaultCache;
//    
//    /**
//     * 设置与缓存服务器的连接池
//     */
//    static {
//        // 服务器列表和其权重
//        String[] servers = { "127.0.0.1:11211" };
//        Integer[] weights = { 3 };
//  
//        // 获取socke连接池的实例对象
//        SockIOPool pool = SockIOPool.getInstance();
//  
//        // 设置服务器信息
//        pool.setServers( servers );
//        pool.setWeights( weights );
//  
//        // 设置初始连接数、最小和最大连接数以及最大处理时间
//        pool.setInitConn( 5 );
//        pool.setMinConn( 5 );
//        pool.setMaxConn( 250 );
//        pool.setMaxIdle( 1000 * 60 * 60 * 6 );
//  
//        // 设置主线程的睡眠时间
//        pool.setMaintSleep( 30 );
//  
//        // 设置TCP的参数，连接超时等
//        pool.setNagle( false );
//        pool.setSocketTO( 3000 );
//        pool.setSocketConnectTO( 0 );
//  
//        // 初始化连接池
//        pool.initialize();
//  
//        // 压缩设置，超过指定大小（单位为K）的数据都会被压缩
////        memcachedClient.setCompressEnable( true );
////        memcachedClient.setCompressThreshold( 64 * 1024 );
//    }
//     
//    /**
//     * 保护型构造方法，不允许实例化！
//     *
//     */
//    protected MemcachedManager()
//    {
//         
//    }
//     
//    /**
//     * 获取唯一实例.
//     * @return
//     */
//    public static MemcachedManager getInstance()
//    {
//        if (memcachedManager != null) {
//            return memcachedManager;
//        }
//        synchronized (MemcachedManager.class) {
//            if (memcachedManager == null) {
//                memcachedManager = new MemcachedManager();
//            }
//            return memcachedManager;
//        }
//    }
//    
//    /**
//     * 
//     * @param name
//     * @return
//     * @throws IllegalStateException
//     * @throws ClassCastException
//     */
//    public IBean getCache(String name) throws IllegalStateException, ClassCastException {
//        return ((this.caches.get(name) instanceof IBean) ? (IBean)this.caches.get(name) : null);
//    }
//    
//    /**
//     * 
//     * @param cacheName
//     * @return
//     */
//    public synchronized IBean addCacheIfAbsent(String cacheName) {
//        if (cacheName == null || cacheName.isEmpty()) {
//            return null;
//        }
//        IBean cache = (IBean) this.caches.get(cacheName);
//        if (cache != null) {
//            return cache;
//        }
//        this.caches.putIfAbsent(cacheName, new MBean());
//        return ((IBean) this.caches.get(cacheName));
//    }
//     
//    /**
//     * 添加一个指定的值到缓存中.
//     * @param key
//     * @param value
//     * @return
//     */
//    public boolean add(String key, Object value)
//    {
//        return memcachedClient.add(key, value);
//    }
//     
//    public boolean add(String key, Object value, Date expiry)
//    {
//        return memcachedClient.add(key, value, expiry);
//    }
//     
//    public boolean replace(String key, Object value)
//    {
//        return memcachedClient.replace(key, value);
//    }
//     
//    public boolean replace(String key, Object value, Date expiry)
//    {
//        return memcachedClient.replace(key, value, expiry);
//    }
//     
//    /**
//     * 根据指定的关键字获取对象.
//     * @param key
//     * @return
//     */
//    public Object get(String key)
//    {
//        return memcachedClient.get(key);
//    }
//     
//    public static void main(String[] args)
//    {
//        MemcachedManager cache = MemcachedManager.getInstance();
//        long startDate=System.currentTimeMillis();
//        for (int i = 0; i < 10000*1000; i++) {
//            cache.add("test"+i , "中国");
//        }
//        long endDate=System.currentTimeMillis();
//          
//        long nowDate=(endDate-startDate)/1000;
//        System.out.println(nowDate);
//        System.out.print( " get value : " + cache.get( "test" ));
//    }
}
