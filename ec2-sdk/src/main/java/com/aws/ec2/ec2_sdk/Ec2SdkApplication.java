package com.aws.ec2.ec2_sdk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.aws.ec2.ec2_sdk.controller") 
public class Ec2SdkApplication {

	public static void main(String[] args) {
		SpringApplication.run(Ec2SdkApplication.class, args);
	}

}
