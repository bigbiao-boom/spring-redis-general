package cff.web.redis.news;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author chenff
* @description @TODO
* @date 2017年3月13日 下午7:26:23
* @version 1.0
*/
@Service
public class RedisList {
	@Autowired
	private RedisTemplateManage redisTemplateManage;
}
