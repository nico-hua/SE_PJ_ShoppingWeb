package com.ss.shoppingweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ss.shoppingweb.mapper")
public class ShoppingWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingWebApplication.class, args);
    }

}
