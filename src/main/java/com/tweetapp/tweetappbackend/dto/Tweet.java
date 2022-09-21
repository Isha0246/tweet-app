package com.tweetapp.tweetappbackend.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "TWEET")
@Component
public class Tweet {

	@Id
	private int tweetId;
	private String userName;
	private String tweet;
	private Date createdDateTime;
	private Map<String, Integer> tweetLikes;
	private Map<String, List<String>> tweetReplies;
}
