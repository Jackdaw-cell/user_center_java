package com.jackdaw.javapro.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RExecutorService;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.RedissonRxClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

/**
 * Redisson配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.redis")  //读取配置文件
public class RedissonConfig {

    private String host;

    private String port;

    @Bean
    public RedissonClient redissonClient() {
//       1、创建配置项
        Config config = new Config();

        String redisAddress=String.format("redis://%s:%s",host,port);

//        单机集群 : 设置redis为本机的redis，redis库为3
        config.useSingleServer().setAddress(redisAddress).setDatabase(3);

//      2. 返回创建redis连接实例
        return Redisson.create(config);
    }
}
