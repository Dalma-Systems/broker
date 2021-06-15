package com.dalma.broker.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.dalma.broker", "com.bybright.logging"})
public class DalmaBrokerApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(DalmaBrokerApiApplication.class, args);
    }
}
