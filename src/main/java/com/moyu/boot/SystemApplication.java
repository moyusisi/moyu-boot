package com.moyu.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author shisong
 * @since 2023-02-21
 */
@SpringBootApplication
public class SystemApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SystemApplication.class);
        application.run(args);
    }
}
