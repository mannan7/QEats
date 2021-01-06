package com.crio.qeats.configs;

import java.time.Duration;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


@Component
public class RedisConfiguration {

  // TODO: CRIO_TASK_MODULE_REDIS
  // The Jedis client for Redis goes through some initialization steps before you can
  // start using it as a cache.
  // Objective:
  // Some methods are empty or partially filled. Make it into a working implementation.
  public static final String redisHost = "localhost";

  // Amount of time after which the redis entries should expire.
  public static final int REDIS_ENTRY_EXPIRY_IN_SECONDS = 3600;

  // TIP(MODULE_RABBITMQ): RabbitMQ related configs.
  public static final String EXCHANGE_NAME = "rabbitmq-exchange";
  public static final String QUEUE_NAME = "rabbitmq-queue";
  public static final String ROUTING_KEY = "qeats.postorder";


  private int redisPort;
  private JedisPool jedisPool;

  public JedisPool getJedisPool() {
    if (jedisPool != null) {
      return jedisPool;
    }

    try {
      final JedisPoolConfig poolConfig = buildPoolConfig();
      jedisPool = new JedisPool(poolConfig, redisHost, redisPort);
    } catch (Exception e) {
      // We don't want to do anything for if cache initialization fails.
      e.printStackTrace();
    }

    return jedisPool;
  }


  @Value("${spring.redis.port}")
  public void setRedisPort(int port) {
    System.out.println("setting up redis port to " + port);
    redisPort = port;
  }

  /**
   * Initializes the cache to be used in the code.
   * TIP: Look in the direction of `JedisPool`.
   */
  @PostConstruct
  public void initCache() {
    final JedisPoolConfig poolConfig = buildPoolConfig();
    try {
      jedisPool = new JedisPool(poolConfig, redisHost);
    } catch (Exception e) {
      // We don't want to do anything for if cache initialization fails.
      e.printStackTrace();
    }
  }

  private static JedisPoolConfig buildPoolConfig() {
    final JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(128);
    poolConfig.setMaxIdle(128);
    poolConfig.setMinIdle(16);
    poolConfig.setTestOnBorrow(true);
    poolConfig.setTestOnReturn(true);
    poolConfig.setTestWhileIdle(true);
    poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
    poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
    poolConfig.setNumTestsPerEvictionRun(3);
    poolConfig.setBlockWhenExhausted(true);
    return poolConfig;
  }

  /**
   * Checks if cache is intiailized and available.
   * TIP: This would generally mean checking via {@link JedisPool }
   * @return true / false if cache is available or not.
   */
  public boolean isCacheAvailable() {
    if (jedisPool == null) {
      return false;
    }

    try (Jedis jedis = getJedisPool().getResource()) {
      return true;
    } catch (Exception e) {
      // e.printStackTrace();
      return false;
    }

  }

  /**
   * Destroy the cache.
   * TIP: This is useful if cache is stale or while performing tests.
   */
  public void destroyCache() {
    if (jedisPool != null) {
      jedisPool.getResource().flushAll();
      jedisPool.destroy();
      jedisPool = null;
    }
  }

}
