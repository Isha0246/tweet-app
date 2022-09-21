package com.tweetapp.tweetappbackend.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TweetResponseDTO {

    List<Tweet> tweets = new ArrayList<Tweet>();

    private boolean success;

    private String responseMessage;

    private String errorMessage;

    public void addTweet(Tweet tweet) {
        tweets.add(tweet);
    }

}
