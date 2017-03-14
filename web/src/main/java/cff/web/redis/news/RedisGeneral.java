package cff.web.redis.news;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.stereotype.Service;

/**
* @author chenff
* @description redis通用操作
* @date 2017年3月13日 下午7:26:23
* @version 1.0
*/
@Service
public class RedisGeneral {
	@Autowired
	private RedisTemplateManage redisTemplateManage;
	
	/**
	 * ****************通用操作**************
	 */

	/**
	 * 根据key设置失效时间
	 */
	public <K extends Serializable> Boolean expire(K key, long expire, int SerializerType) {
		return redisTemplateManage.get().expire(key, expire, TimeUnit.SECONDS);
	}

	/**
	 * 根据key获取失效时间
	 */
	public <K extends Serializable> Long getExpire(K key, int SerializerType) {
		return redisTemplateManage.get().getExpire(key, TimeUnit.SECONDS);
	}

	/**
	 * 判断是否有key
	 * 
	 * @param key
	 */
	public Boolean hasKey(String key, int SerializerType) {
		return redisTemplateManage.get().hasKey(key);
	}

	/**
	 * 获取数据
	 */
	@SuppressWarnings("unchecked")
	public <V extends Serializable, K> V get(K key, int SerializerType) {
		return (V) redisTemplateManage.get().opsForValue().get(key);
	}

	// 删除键
	public <K extends Serializable> void del(K key, int SerializerType) {
		redisTemplateManage.get().delete(key);
	}

	// 刷新db
	public Boolean flushDB(int SerializerType) {
		return redisTemplateManage.get().execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				connection.flushDb();
				return true;
			}
		});
	}

	public Long dbSize(int SerializerType) {
		return redisTemplateManage.get().execute(new RedisCallback<Long>() {
			@Override
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.dbSize();
			}
		});
	}

	// 获取keys
	public Set<Serializable> keys(String pattern, int SerializerType) {
		return redisTemplateManage.get().keys(pattern);
	}
}
