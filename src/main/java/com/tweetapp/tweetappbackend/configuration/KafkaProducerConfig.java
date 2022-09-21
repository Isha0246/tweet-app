package com.tweetapp.tweetappbackend.configuration;

import com.tweetapp.tweetappbackend.utility.TweetAppConstants;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@Service
public class KafkaProducerConfig {

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	public void sendMessage(String message) {
		log.info("MESSAGE SENT BY PRODUCER -> " +message);
		this.kafkaTemplate.send(TweetAppConstants.TOPIC_NAME, TweetAppConstants.TOPIC_NAME, message);
	}

	@Bean
	public NewTopic createTopic() {
		return new NewTopic(TweetAppConstants.TOPIC_NAME, 3, (short) 1);
	}

}
