package com.tweetapp.tweetappbackend.service;

import com.tweetapp.tweetappbackend.dto.User;
import com.tweetapp.tweetappbackend.dto.UserResponseDTO;
import com.tweetapp.tweetappbackend.exception.TweetAppException;

public interface UserService {

    UserResponseDTO loginUser(String userName, String password) throws TweetAppException;

    UserResponseDTO registerUser(User user) throws TweetAppException;

    UserResponseDTO getUser(String userName) throws TweetAppException;

    UserResponseDTO getAllUsers() throws TweetAppException;

    UserResponseDTO forgotPassword(String userName, String password) throws TweetAppException;
}
