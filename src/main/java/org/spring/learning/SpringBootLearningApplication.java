package org.spring.learning;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class SpringBootLearningApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootLearningApplication.class, args);
	}

	/*@Bean
    public CommandLineRunner run(ApplicationContext context){
	    return a -> {
	        String[]beans = context.getBeanDefinitionNames();
	        Arrays.stream(beans).forEach(System.out::println);
        };
    }*/
}
