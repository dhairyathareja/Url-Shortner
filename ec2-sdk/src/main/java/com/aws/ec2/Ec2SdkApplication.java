package com.aws.ec2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.aws.ec2.controller") // Ensure Spring scans your controller
public class Ec2SdkApplication {
    public static void main(String[] args) {
        SpringApplication.run(Ec2SdkApplication.class, args);
    }
}
