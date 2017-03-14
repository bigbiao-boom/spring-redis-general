package cff.web.redis.news;


import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

/**
 * @author chenff
 * @description 模板管理
 * @date 2017年3月13日 下午6:38:30
 * @version 1.0
 */
@Service
public class RedisTemplateManage {
	@Autowired
	private RedisContext redisContext;

	private RedisTemplate<Serializable, Object> redisTemplate;

	public int SERIALIZER_JDK = 0;
	public int SERIALIZER_STRING = 1;

	public RedisTemplate<Serializable, Object> get() {
		if (redisTemplate == null) {
			redisTemplate = redisContext.redisTemplate();
		}
		return redisTemplate;
	}

	public RedisTemplate<Serializable, Object> setSerializerType(int serializerType) {
		redisTemplate = get();
		if (SERIALIZER_STRING == serializerType) {
			redisTemplate.setKeySerializer(new StringRedisSerializer(StandardCharsets.UTF_8));
			redisTemplate.setValueSerializer(new StringRedisSerializer(StandardCharsets.UTF_8));
			redisTemplate.setHashKeySerializer(new StringRedisSerializer(StandardCharsets.UTF_8));
			redisTemplate.setHashValueSerializer(new StringRedisSerializer(StandardCharsets.UTF_8));
		} else {
			redisTemplate.setKeySerializer(new StringRedisSerializer(StandardCharsets.UTF_8));
			redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
			redisTemplate.setHashKeySerializer(new StringRedisSerializer(StandardCharsets.UTF_8));
			redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
		}
		return redisTemplate;
	}
}
