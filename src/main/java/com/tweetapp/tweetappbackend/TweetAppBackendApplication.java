package com.tweetapp.tweetappbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class TweetAppBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TweetAppBackendApplication.class, args);
	}

}
