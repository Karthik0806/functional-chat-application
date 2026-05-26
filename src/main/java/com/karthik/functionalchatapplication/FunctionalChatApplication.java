package com.karthik.functionalchatapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FunctionalChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(FunctionalChatApplication.class, args);
    }


}
