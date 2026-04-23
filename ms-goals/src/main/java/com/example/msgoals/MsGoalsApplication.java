package com.example.msgoals;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsGoalsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsGoalsApplication.class, args);
    }

}
