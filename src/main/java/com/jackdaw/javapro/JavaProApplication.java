package com.jackdaw.javapro;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.spring.web.SpringfoxWebMvcConfiguration;


@ConditionalOnClass(SpringfoxWebMvcConfiguration.class)
@SpringBootApplication
@MapperScan("com.jackdaw.javapro.mapper")
@EnableScheduling
public class JavaProApplication {


    public static void main(String[] args) {

        SpringApplication.run(JavaProApplication.class, args);

    }

}
