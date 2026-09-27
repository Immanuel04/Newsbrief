package com.project.Newsbrief;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class NewsbriefApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsbriefApplication.class, args);
	}
}
