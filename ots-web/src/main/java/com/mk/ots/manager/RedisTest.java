package com.mk.ots.manager;

import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisTest {

	public static JedisPool pool = new JedisPool(new JedisPoolConfig(), "192.168.0.120");

	public static void main(String[] args) {

		Jedis jedis = pool.getResource();
		try {
			// 随便做一些对于redis的操作
			jedis.set("foo", "bar");
			String foobar = jedis.get("foo");
			jedis.zadd("sose", 0, "car");
			jedis.zadd("sose", 0, "bike");
			Set<String> sose = jedis.zrange("sose", 0, -1);
		} finally {
			// 这里很重要，一旦拿到的jedis实例使用完毕，必须要返还给池中
			pool.returnResource(jedis);
		}
		// 程序关闭时，需要调用关闭方法
		pool.destroy();
	}

}
