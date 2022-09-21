package com.tweetapp.tweetappbackend.request;

import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Component
@Getter
@Setter
@ToString
public class TweetRequest {
	@Id
	private int tweetId;
	@NotBlank(message = "User name cannot be Null")
	private String userName;
	@NotBlank(message = "Tweet cannot be Null")
	private String tweet;
}