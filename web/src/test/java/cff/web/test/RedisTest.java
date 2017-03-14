package cff.web.test;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cff.web.redis.news.RedisGeneral;
import cff.web.redis.news.RedisString;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:conf/spring-*.xml" })
public class RedisTest extends AbstractTest {
	
	@Autowired
	private RedisString redisString;
	@Autowired
	RedisGeneral redisGeneral;
	@Test
	public void test(){
		redisString.set("cfff", "cfff", 1);
		String aaa=redisGeneral.get("cfff", 1);
		System.out.println(aaa);
	}
}
