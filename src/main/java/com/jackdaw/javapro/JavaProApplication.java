package com.jackdaw.javapro;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.jackdaw.javapro.mapper")
public class JavaProApplication {


    public static void main(String[] args) {

        SpringApplication.run(JavaProApplication.class, args);

    }

}
