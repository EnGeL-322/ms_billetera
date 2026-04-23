package com.example.msevents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsEventsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsEventsApplication.class, args);
    }

}
