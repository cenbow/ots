package com.mk.ots.order.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.mk.es.entities.OtsHotel;
import com.mk.framework.es.ElasticsearchProxy;
import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.BeanException;
import com.mk.orm.plugin.bean.Db;
import com.mk.ots.domain.IBean;
import com.mk.ots.manager.OtsCacheManager;
import com.mk.ots.model.BOtaOrder;

/**
 * Remote Order Service
 */
@Service
public class RemoteOrderService {
    public static final String TABLE_OTAORDER = "u_member";
    
    @Resource
    private CacheManager cacheManager;
    
    @Resource
    private OtsCacheManager manager;
    
    @Resource
    private ElasticsearchProxy esProxy;
    
    /**
     * 根据订单id主键查询订单详细信息
     * @param params
     * 参数：方法参数数据
     * @return Bean
     * 返回值
     * @throws BeanException
     * 捕获异常：BeanException
     */
    public Bean byId(IBean params) throws BeanException {
        //
        if (1==1) {
            return null;
        }
        
        // ES Store test
        postEsByProxy();
        ////postMsgToES();
        
        // 从缓存中得到缓存名、key为otsname的缓存值
        Object cacheValue = manager.get("_ots_", "otsname");
        if (cacheValue == null) {
            System.out.println("-==== otsname init is: ====-");
            // 如果不存在指定key的缓存，put一个值
            manager.put("_ots_", "otsname", "welcome mk.");
            System.out.println("welcome mk.");
        }
        // 如果存在则直接返回值
        System.out.println("-==== value output is: ====-");
        System.out.println(cacheValue);
        
        //// Db batch methods for example
        // batch insert
        String sql = "insert b_otaorder values (?, ?, ?, ?, ?, ?)";
        String columns = "ordername, hotelid, hotelname, begindate, enddate, ordertype";
        List<Bean> modelOrBeanList = null;
        int batchSize = 50;
        ////Db.batch(sql, columns, modelOrBeanList, batchSize);
        // batch delete
        sql = "delete from b_otaorder where pid = ?";
        ////Db.update(sql, "a123");
        // batch update
        sql = "update b_otaorder set ordertype = ? where hotelid = ?";
        ////Db.update(sql, "T", "H201321");
        
        // Model test
        modelTest();
        
        // 缓存过期时间测试
        testCacheExpires();
        
        // 通过Bean查询数据
        Bean bean = Db.findById(TABLE_OTAORDER, "mid", params.get("mid", 0));
        return bean;
    }
    
    /**
     * 
     * @param params
     * @return
     * @throws BeanException
     */
    public Bean byId(Map<String, String> params) throws BeanException {
        Bean bean = Db.findById(TABLE_OTAORDER, params.get("id"));
        return bean;
    }
    
    /**
     * 
     */
    private void modelTest() {
        manager.getCacheManager();
        BOtaOrder order = BOtaOrder.dao.findFirst("select * from b_otaorder limit 0,10");
        order.set("hotelname", "helll");
        order.update();
        System.out.println(order);
    }
    
    private void testCacheExpires() {
        String cacheName = "ots";
        String key = "abc";
        manager.setExpires(cacheName, key, "我5秒后就过期了", 5);
        Object vl = manager.getExpires(cacheName, key);
        System.out.println(vl);
        
        System.out.println("cached Bean Object .....");
        Bean bean = new Bean();
        bean.set("column1", "c001").set("column2", "value2");
        manager.setExpires(cacheName, key.concat("~obj"), bean, 3);
        Bean cacheBean = (Bean) manager.getExpiresObject(cacheName, key.concat("~obj"));
        System.out.println(cacheBean.getColumns());
        try {
            Thread.sleep(6000);
            vl = manager.getExpires(cacheName, key);
            if (vl == null) {
                System.out.println("我已经过期了.");
            } else {
                System.out.println("=================");
                System.out.println(vl);
                System.out.println("=================");
                System.out.println("我不是过期了吗？");
            }
            Object obj = manager.getExpiresObject(cacheName, key.concat("~obj"));
            if (obj == null) {
                System.out.println("Bean对象已经过期了.");
            } else {
                System.out.println("=================");
                System.out.println(((Bean)obj).getColumns());
                System.out.println("=================");
                System.out.println("对象不是过期了吗？");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
    /**
     * 
     */
    private void postEsByProxy() {
        String sql = "select * from t_hotel";
        Collection<Object> coll = new ArrayList<Object>();
        try {
            List<Bean> hotels = Db.find(sql);
            for (Bean bean : hotels) {
                OtsHotel hotel = new OtsHotel(bean);
                coll.add(hotel);
            }
            esProxy.batchAddDocument(coll);
        } catch (Exception e) {
            System.err.println("post es error:\n" + e.getMessage());
        }
    }
    
    /**
     * 
     */
    private void postMsgToES() {
        //
        String clusterName = "elasticsearch";
        String clusterHosts = "localhost:9300";
        
        String indexName = "ots";
        String indexType = "hotel";
        String msgVal = "hello ots.";
        
        System.out.println("store hotel data to ES...");
        if (indexName != null && indexType != null && msgVal != null
                && indexName.trim().length() > 0 && indexType.trim().length() > 0 && msgVal.trim().length() > 0) {
            System.out.println("indexName = "+ indexName + ",indexType = " + indexType +",msgVal = " + msgVal);
            Settings settings = null;
            TransportClient client = null;
            try {
                settings = ImmutableSettings.settingsBuilder().put("cluster.name", clusterName).build();
                client = new TransportClient(settings);
                String[] hostsSplit = clusterHosts.split(",");
                if (hostsSplit != null) {
                    for (String hostInfo : hostsSplit) {
                        int flgPos = hostInfo.indexOf(":");
                        if (flgPos > -1) {
                            String host = hostInfo.substring(0, flgPos).trim();
                            int port = Integer.parseInt(hostInfo.substring(flgPos + 1).trim());
                            client.addTransportAddress(new InetSocketTransportAddress(host, port));
                        }
                    }
                }
                
                BulkRequestBuilder bulkRequest = client.prepareBulk();
                //// bulkRequest.add(client.prepareIndex(indexName, indexType).setSource(msgVal));
                String sql = "select * from t_hotel";
                List<Bean> hotels = Db.find(sql);
                
                for (Bean bean : hotels) {
                    OtsHotel hotel = new OtsHotel(bean);
                    bulkRequest.add(client.prepareIndex(indexName, indexType).setSource(hotel));
                }
                try {
                    if (bulkRequest.numberOfActions() == 0) {
                        return;
                    }
                    bulkRequest.execute().actionGet();
                    System.out.println("message text already posted to es.");
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            } finally {
                // 关闭客户端连接
                if (client != null) {
                    client.close();
                }
            }
        }
    }
    
//    /**
//     * send sms message test.
//     * @return
//     */
//    private String testSmsMessage() {
//        String result = "";
//        System.out.println("test sms message begin...");
//        try {
//            String sn = SysConfigManager.getInstance().readOne(Constant.systype, Constant.messagesn);
//            System.out.println("sn is :" + sn);
//            
//            String password = SysConfigManager.getInstance().readOne(Constant.systype, Constant.messagepsw);
//            System.out.println("password is :" + password);
//            
//            String serviceUrl = SysConfigManager.getInstance().readOne(Constant.systype, Constant.messageUrl);
//            System.out.println("serviceUrl is :" + serviceUrl);
//            
//            ITips message = new SmsMessage(sn, password, serviceUrl);
//            message.setReceiver("15801209201").setContent("hello,nolan.");
//            result = MessageProxy.sendMessage(message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
//    
//    /**
//     * send voice message test.
//     * @return
//     */
//    private String testVoiceMessage() {
//        String result = "";
//        System.out.println("test voice message begin...");
//        try {
//            String sn = SysConfigManager.getInstance().readOne(Constant.systype, Constant.messagesn);
//            System.out.println("sn is :" + sn);
//            
//            String password = SysConfigManager.getInstance().readOne(Constant.systype, Constant.messagepsw);
//            System.out.println("password is :" + password);
//            
//            String serviceUrl = SysConfigManager.getInstance().readOne(Constant.systype, Constant.messageAudioUrl);
//            System.out.println("serviceUrl is :" + serviceUrl);
//            
//            ITips message = new VoiceMessage(sn, password, serviceUrl);
//            message.setTitle("测试语音").setReceiver("1580209201").setContent("语音短信测试");
//            result = MessageProxy.sendMessage(message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
}
