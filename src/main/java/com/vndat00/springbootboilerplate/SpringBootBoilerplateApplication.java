package com.vndat00.springbootboilerplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringBootBoilerplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootBoilerplateApplication.class, args);
    }

}
