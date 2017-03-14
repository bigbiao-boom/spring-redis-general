package cff.web.redis.news;


import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

/**
* @author chenff
* @description redis的sorted操作
* @date 2017年3月13日 下午7:26:23
* @version 1.0
*/
@Service
public class RedisSortedSet {
	@Autowired
	private RedisTemplateManage redisTemplateManage;
	
	/**
	 ***************** sort操作**************
	 */

	public void zaddSort(String key, Set<TypedTuple<Object>> sorts, int SerializerType) {
		redisTemplateManage.get().opsForZSet().add(key, sorts);
	}

	// 获取有序集合的成员数
	public Long zcardSort(String key, int SerializerType) {
		return redisTemplateManage.get().opsForZSet().zCard(key);
	}

	// 返回有序集中指定区间内的成员，通过索引，分数从高到底
	@SuppressWarnings("unchecked")
	public <T> Set<T> zrevrange(String key, long start, long end, int SerializerType) {
		return (Set<T>) redisTemplateManage.get().opsForZSet().reverseRange(key, start, end);
	}
	
	@SuppressWarnings("unchecked")
	// 通过索引区间返回有序集合成指定区间内的成员
	public <T> Set<T> zrange(String key, long start, long end, int SerializerType) {
		return (Set<T>) redisTemplateManage.get().opsForZSet().range(key, start, end);
	}

	// 移除有序集合中的一个或多个成员
	public void zrem(String key, int SerializerType, Object... values) {
		redisTemplateManage.get().opsForZSet().remove(key, values);
	}

	// 移除有序集合中给定的排名区间的所有成员
	public Long removeRangeByScore(String key, double min, double max, int SerializerType) {
		return redisTemplateManage.get().opsForZSet().removeRangeByScore(key, min, max);
	}
	
	// 移除区间内的元素
	public Long removeRange(String key, long start, long end, int SerializerType){
		return redisTemplateManage.get().opsForZSet().removeRange(key, start, end);
	}
	
	//
	public void zadd(String key, Object obj, double score, int SerializerType) {
		if (obj == null) {
			return;
		}
		redisTemplateManage.get().opsForZSet().add(key, obj, score);
	}
}
