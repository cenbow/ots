package com.mk.framework.component.cache;
//package com.mk.ots.component.cache;
//
//import java.net.UnknownHostException;
//import java.util.List;
//
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.data.mongodb.core.query.Update;
//
//import com.google.common.collect.Lists;
//import com.mongodb.MongoClient;
//import com.mongodb.ServerAddress;
//
///**
// * @author nolan
// * @param <T>
// *
// */
//public class DBCacheManager {
//	
//	private static MongoTemplate mongoTemplate;
//	 
//	private DBCacheManager() {
//		try {
//			String database = "mk_db";
//			List<ServerAddress> serverList = Lists.newArrayList();
//			serverList.add(new ServerAddress("localhost", 27017));
//			MongoClient mongoClient = new MongoClient(serverList);
//			mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(mongoClient, database));
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	
//	/**
//	 * @return
//	 */
//	public static DBCacheManager getInstance(){
//		return SingleHolder.dbCacheManager;
//	}
//	
//	/**
//	 * @author nolan
//	 *
//	 */
//	private static class SingleHolder {
//		private static DBCacheManager dbCacheManager = new DBCacheManager();
//	}
//
//	public <T> List<T> find(Query query, Class<T> classzz) {
//		return mongoTemplate.find(query, classzz);
//	}
//
//	public <T> T findOne(Query query, Class<T> classzz) {
//		return mongoTemplate.findOne(query, classzz);
//	}
//	 
//	public void update(Query query, Update update, Class classzz) {
//		mongoTemplate.upsert(query, update, classzz);
//	}
//
//	public void save(Object bean) {
//		mongoTemplate.save(bean);
//	}
//
//	public void remove(Object bean){
//		mongoTemplate.remove(bean);
//	}
//	
//	public <T> T get(String id, Class<T> classzz) {
//		return mongoTemplate.findById(id, classzz);
//	}
//}
