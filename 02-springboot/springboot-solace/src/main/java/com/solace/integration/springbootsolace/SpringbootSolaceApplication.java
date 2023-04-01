package com.solace.integration.springbootsolace;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringbootSolaceApplication {

	public static void main(String[] args) {		
		var ctx = SpringApplication.run(SpringbootSolaceApplication.class, args);
		Arrays.asList(ctx.getBeanDefinitionNames()).forEach(System.out::println);
	}

}
