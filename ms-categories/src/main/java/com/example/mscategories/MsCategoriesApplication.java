package com.example.mscategories;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsCategoriesApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsCategoriesApplication.class, args);
    }

}
