package cff.web.redis.old;


import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

@Service
public class RedisManager {
	@Autowired
	private RedisContext redisContext;

	public static int SERIALIZER_JDK = 0;
	public static int SERIALIZER_STRING = 1;

	private RedisTemplate<Serializable, Object> redisTemplate(int SerializerType) {
		RedisTemplate<Serializable, Object> redisTemplate = redisContext.redisTemplate();
		if (RedisManager.SERIALIZER_STRING == SerializerType) {
			redisTemplate.setKeySerializer(new StringRedisSerializer(StandardCharsets.UTF_8));
			redisTemplate.setValueSerializer(new StringRedisSerializer(StandardCharsets.UTF_8));
			redisTemplate.setHashKeySerializer(new StringRedisSerializer(StandardCharsets.UTF_8));
			redisTemplate.setHashValueSerializer(new StringRedisSerializer(StandardCharsets.UTF_8));
		} else {
			redisTemplate.setKeySerializer(new JdkSerializationRedisSerializer());
			redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
			redisTemplate.setHashKeySerializer(new JdkSerializationRedisSerializer());
			redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
		}
		return redisTemplate;
	}

	/**
	 * ****************通用操作**************
	 */

	/**
	 * 根据key设置失效时间
	 */
	public <K extends Serializable> Boolean expire(K key, long expire, int SerializerType) {
		return redisTemplate(SerializerType).expire(key, expire, TimeUnit.SECONDS);
	}

	/**
	 * 根据key获取失效时间
	 */
	public <K extends Serializable> Long getExpire(K key, int SerializerType) {
		return redisTemplate(SerializerType).getExpire(key, TimeUnit.SECONDS);
	}

	/**
	 * 判断是否有key
	 * 
	 * @param key
	 */
	public Boolean hasKey(String key, int SerializerType) {
		return redisTemplate(SerializerType).hasKey(key);
	}

	/**
	 * 获取数据
	 */
	@SuppressWarnings("unchecked")
	public <V extends Serializable, K> V get(K key, int SerializerType) {
		return (V) redisTemplate(SerializerType).opsForValue().get(key);
	}

	// 删除键
	public <K extends Serializable> void del(K key, int SerializerType) {
		redisTemplate(SerializerType).delete(key);
	}

	// 刷新db
	public Boolean flushDB(int SerializerType) {
		return redisTemplate(SerializerType).execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				connection.flushDb();
				return true;
			}
		});
	}

	public Long dbSize(int SerializerType) {
		return redisTemplate(SerializerType).execute(new RedisCallback<Long>() {
			@Override
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.dbSize();
			}
		});
	}

	// 获取keys
	public Set<Serializable> keys(String pattern, int SerializerType) {
		return redisTemplate(SerializerType).keys(pattern);
	}

	/**
	 * ****************String操作**************
	 */

	// 存redis
	public void set(String key, String value, int SerializerType) {
		redisTemplate(SerializerType).opsForValue().set(key, value);
	}

	// 递增
	public <K extends Serializable> Long incrBy(K key, long delta, int SerializerType) {
		return redisTemplate(SerializerType).opsForValue().increment(key, delta);
	}

	// 递减
	public <K extends Serializable> Long decrBy(K key, long delta, int SerializerType) {
		return redisTemplate(SerializerType).opsForValue().increment(key, -delta);
	}

	/**
	 * ****************Hash操作**************
	 */

	// 存map
	public <K, V> void putHashMap(String key, Map<K, V> dataMap, int SerializerType) {
		HashOperations<Serializable, Object, Object> hashOperations = redisTemplate(SerializerType).opsForHash();
		if (null != dataMap && !dataMap.isEmpty()) {
			for (Map.Entry<K, V> entry : dataMap.entrySet()) {
				hashOperations.put(key, entry.getKey(), entry.getValue());
			}
		}
	}

	// 获取map
	public <HK, HV> Map<HK, HV> getHashMap(String key, int SerializerType) {
		@SuppressWarnings("unchecked")
		Map<HK, HV> map = (Map<HK, HV>) redisTemplate(SerializerType).opsForHash().entries(key);
		return map;
	}

	// 获取hashmap的一个字段值
	public Object getHashMapFiled(String key, String filed, int SerializerType) {
		Map<Object, Object> map = redisTemplate(SerializerType).opsForHash().entries(key);
		return map.get(filed);
	}

	// 设置hashmap的一个字段值
	public void setHashMapFiled(String key, String filed, Object value, int SerializerType) {
		redisTemplate(SerializerType).opsForHash().putIfAbsent(key, filed, value);
	}

	// 删除mapfiled
	public void delHashMapFiled(String key, int SerializerType, Object... fileds) {
		redisTemplate(SerializerType).opsForHash().delete(key, fileds);
	}

	/**
	 ***************** sort操作**************
	 */

	public void zaddSort(String key, Set<TypedTuple<Object>> sorts, int SerializerType) {
		redisTemplate(SerializerType).opsForZSet().add(key, sorts);
	}

	// 获取有序集合的成员数
	public Long zcardSort(String key, int SerializerType) {
		return redisTemplate(SerializerType).opsForZSet().zCard(key);
	}

	// 返回有序集中指定区间内的成员，通过索引，分数从高到底
	@SuppressWarnings("unchecked")
	public <T> Set<T> zrevrange(String key, long start, long end, int SerializerType) {
		return (Set<T>) redisTemplate(SerializerType).opsForZSet().reverseRange(key, start, end);
	}
	
	@SuppressWarnings("unchecked")
	// 通过索引区间返回有序集合成指定区间内的成员
	public <T> Set<T> zrange(String key, long start, long end, int SerializerType) {
		return (Set<T>) redisTemplate(SerializerType).opsForZSet().range(key, start, end);
	}

	// 移除有序集合中的一个或多个成员
	public void zrem(String key, int SerializerType, Object... values) {
		redisTemplate(SerializerType).opsForZSet().remove(key, values);
	}

	// 移除有序集合中给定的排名区间的所有成员
	public Long removeRangeByScore(String key, double min, double max, int SerializerType) {
		return redisTemplate(SerializerType).opsForZSet().removeRangeByScore(key, min, max);
	}
	
	// 移除区间内的元素
	public Long removeRange(String key, long start, long end, int SerializerType){
		return redisTemplate(SerializerType).opsForZSet().removeRange(key, start, end);
	}
	
	//
	public void zadd(String key, Object obj, double score, int SerializerType) {
		if (obj == null) {
			return;
		}
		redisTemplate(SerializerType).opsForZSet().add(key, obj, score);
	}
}
