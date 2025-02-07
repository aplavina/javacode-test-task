package com.aplavina.test_task_wallets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class TestTaskWalletsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestTaskWalletsApplication.class, args);
    }
}
