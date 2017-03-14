package cff.web.redis.news;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author chenff
* @description redis的String操作
* @date 2017年3月13日 下午7:26:23
* @version 1.0
*/
@Service
public class RedisString {
	
	@Autowired
	private RedisTemplateManage redisTemplateManage;
	/**
	 * ****************String操作**************
	 */

	// 存redis
	public void set(String key, String value, int SerializerType) {
		redisTemplateManage.get().opsForValue().set(key, value);
	}

	// 递增
	public <K extends Serializable> Long incrBy(K key, long delta, int SerializerType) {
		return redisTemplateManage.get().opsForValue().increment(key, delta);
	}

	// 递减
	public <K extends Serializable> Long decrBy(K key, long delta, int SerializerType) {
		return redisTemplateManage.get().opsForValue().increment(key, -delta);
	}
}
