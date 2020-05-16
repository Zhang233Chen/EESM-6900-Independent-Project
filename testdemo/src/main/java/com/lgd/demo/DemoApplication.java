package com.lgd.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    private Jedis jedis;

    private JedisPoolConfig config;

    private JedisShardInfo sharInfo;

    @Bean
    public Jedis jedis(){
        config = new JedisPoolConfig();
        config.setMaxIdle(1000);//set max idle time for thread pool
        config.setMaxWaitMillis(1000); //set max wait time for thread pool
        config.setMaxTotal(500); // the max object number in redis
        sharInfo = new JedisShardInfo("127.0.0.1", 6379);
        sharInfo.setConnectionTimeout(5000);
        jedis = new Jedis(sharInfo);
        return jedis;
    }

}
