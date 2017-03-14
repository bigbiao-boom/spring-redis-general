package cff.web.redis.news;

import java.io.Serializable;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

/**
* @author chenff
* @description redis的hash操作
* @date 2017年3月13日 下午7:26:23
* @version 1.0
*/
@Service
public class RedisHash {
	@Autowired
	private RedisTemplateManage redisTemplateManage;
	
	/**
	 * ****************Hash操作**************
	 */

	// 存map
	public <K, V> void putHashMap(String key, Map<K, V> dataMap, int SerializerType) {
		HashOperations<Serializable, Object, Object> hashOperations = redisTemplateManage.get().opsForHash();
		if (null != dataMap && !dataMap.isEmpty()) {
			for (Map.Entry<K, V> entry : dataMap.entrySet()) {
				hashOperations.put(key, entry.getKey(), entry.getValue());
			}
		}
	}

	// 获取map
	public <HK, HV> Map<HK, HV> getHashMap(String key, int SerializerType) {
		@SuppressWarnings("unchecked")
		Map<HK, HV> map = (Map<HK, HV>) redisTemplateManage.get().opsForHash().entries(key);
		return map;
	}

	// 获取hashmap的一个字段值
	public Object getHashMapFiled(String key, String filed, int SerializerType) {
		Map<Object, Object> map = redisTemplateManage.get().opsForHash().entries(key);
		return map.get(filed);
	}

	// 设置hashmap的一个字段值
	public void setHashMapFiled(String key, String filed, Object value, int SerializerType) {
		redisTemplateManage.get().opsForHash().putIfAbsent(key, filed, value);
	}

	// 删除mapfiled
	public void delHashMapFiled(String key, int SerializerType, Object... fileds) {
		redisTemplateManage.get().opsForHash().delete(key, fileds);
	}
}
