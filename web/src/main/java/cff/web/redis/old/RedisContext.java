package cff.web.redis.old;



import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import cff.web.disconf.ConfPropertiesBean;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisContext  {

	private String hostName;
	
	private Integer port;
	
	private String password;

	private JedisPoolConfig poolConfig;
	
	@Autowired
	@Qualifier("jedisConnectionFactory")
	RedisConnectionFactory connectionFactory;
	
	private RedisSentinelConfiguration sentinelConfig;
	
	{
		String master = ConfPropertiesBean.getValue("notify.redis.masterName");
		String sentinelNodes = ConfPropertiesBean.getValue("notify.redis.hosts");
		String redisNodes = ConfPropertiesBean.getValue("notify.redis.nodes");
		String password = ConfPropertiesBean.getValue("notify.redis.password");
		String maxIdle = ConfPropertiesBean.getValue("notify.redis.maxIdle");
		String maxTotal=ConfPropertiesBean.getValue("notify.redis.maxTotal", "-1");
		
		String maxActive = ConfPropertiesBean.getValue("notify.redis.maxActive");
		String maxWait = ConfPropertiesBean.getValue("notify.redis.maxWait");
		String testOnBorrow = ConfPropertiesBean.getValue("notify.redis.testOnBorrow");
		this.setPassword(password);
		if (StringUtils.isNotBlank(sentinelNodes)) {
			sentinelConfig = new RedisSentinelConfiguration().master(master);
			String[] sentinelNodeList = sentinelNodes.split(",");
			for (String sentinelNode : sentinelNodeList) {
				String[] info = sentinelNode.split(":");
				sentinelConfig.sentinel(info[0], Integer.valueOf(info[1]));
			}
		} else if (StringUtils.isNotBlank(redisNodes)) {
			poolConfig = new JedisPoolConfig();
			if (StringUtils.isNotBlank(maxIdle)) {
				poolConfig.setMaxIdle(Integer.valueOf(maxIdle));
			}
			
			if (StringUtils.isNotBlank(maxActive)) {
				poolConfig.setMaxTotal(Integer.valueOf(maxActive));
			} 
			if (StringUtils.isNotBlank(maxWait)) {
				poolConfig.setMaxWaitMillis(Long.valueOf(maxWait));
			}
			
			if (StringUtils.isNoneBlank(testOnBorrow)) {
				poolConfig.setTestOnBorrow(Boolean.valueOf(testOnBorrow));
			}
			if (StringUtils.isNoneBlank(maxTotal)) {
				poolConfig.setMaxTotal(Integer.valueOf(maxTotal));
			}
			
			String[] info = redisNodes.split(":");
			if (info.length != 2) {
				throw new RuntimeException("redis.nodes格式 192.168.0.2:6379");
			}
			this.setHostName(info[0]);
			this.setPort(Integer.valueOf(info[1]));
			
		} else {
			throw new RuntimeException("无法加载redis配置");
		}
	}
	
	@Bean(name="jedisConnectionFactory")
	public RedisConnectionFactory  connectionFactory() { 
		RedisSentinelConfiguration sentinelConfig = sentinelConfig();
		JedisConnectionFactory connectionFactory;
		if (sentinelConfig != null) {
			connectionFactory = new JedisConnectionFactory(sentinelConfig);
		} else {
			connectionFactory = new JedisConnectionFactory(); 
			connectionFactory.setPoolConfig(poolConfig);
			connectionFactory.setHostName(hostName);
			connectionFactory.setPort(port);
			connectionFactory.setUsePool(true);
		}
		connectionFactory.setDatabase(Integer.valueOf(ConfPropertiesBean.getValue("notify.redis.receive.select","2")));
		connectionFactory.setPassword(password);
		this.connectionFactory = connectionFactory;
		return this.connectionFactory;
	}

	public @Bean RedisSentinelConfiguration sentinelConfig() { 
		return sentinelConfig;
	}
	
	@Bean(name="redisTempalte")
	public RedisTemplate<Serializable, Object> redisTemplate() {
		RedisTemplate<Serializable, Object> redisTemplate = new RedisTemplate<Serializable, Object>();
		redisTemplate.setConnectionFactory(connectionFactory);  
		return redisTemplate;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public JedisPoolConfig getPoolConfig() {
		return poolConfig;
	}

	public void setPoolConfig(JedisPoolConfig poolConfig) {
		this.poolConfig = poolConfig;
	}
}