package com.tweetapp.tweetappbackend.service;

import com.tweetapp.tweetappbackend.dto.TweetResponseDTO;
import com.tweetapp.tweetappbackend.exception.TweetAppException;
import com.tweetapp.tweetappbackend.request.TweetRequest;

public interface TweetService {

    TweetResponseDTO postTweet(String userName, TweetRequest tweetRequest) throws TweetAppException;

    TweetResponseDTO getAllTweets() throws TweetAppException;

    TweetResponseDTO getAllUserTweets(String userName) throws TweetAppException;

    TweetResponseDTO updateTweet(String userName, int tweetId, TweetRequest tweetRequest)
            throws TweetAppException;

    TweetResponseDTO likeTweet(String userName, int tweetId) throws TweetAppException;

    TweetResponseDTO replyTweet(String userName, int tweetId, String reply) throws TweetAppException;

    TweetResponseDTO deleteTweet(String userName, int tweetId) throws TweetAppException;

}
