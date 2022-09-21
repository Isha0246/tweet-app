package com.tweetapp.tweetappbackend.controller;

import java.util.List;

import javax.validation.Valid;

import com.tweetapp.tweetappbackend.dto.User;
import com.tweetapp.tweetappbackend.dto.UserResponseDTO;
import com.tweetapp.tweetappbackend.exception.TweetAppException;
import com.tweetapp.tweetappbackend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import lombok.Generated;

@RestController
@RequestMapping(value = "/api/v1.0/tweets")
@Generated
@CrossOrigin(origins= "http://localhost:4200")
public class UserController {

	@Autowired
	UserService userService;

	@GetMapping(value = "/login")
	public ResponseEntity<UserResponseDTO> loginUser(@RequestParam("emailId") String emailId,
			@RequestParam("password") String password) throws TweetAppException {
		final UserResponseDTO userResponseDTO = userService.loginUser(emailId, password);
		if (!userResponseDTO.isSuccess() && userResponseDTO.getErrorMessage() != null
				&& userResponseDTO.getErrorMessage().length() > 0) {
			return new ResponseEntity<>(userResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
	}

	@PostMapping(value = "/register")
	public ResponseEntity<UserResponseDTO> registerUser(@RequestBody @Valid User user) throws TweetAppException {
		final UserResponseDTO userResponseDTO = userService.registerUser(user);
		if (!userResponseDTO.isSuccess() && userResponseDTO.getErrorMessage() != null
				&& userResponseDTO.getErrorMessage().length() > 0) {
			return new ResponseEntity<>(userResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
	}

	@GetMapping(value = "/users/search")
	public ResponseEntity<UserResponseDTO> getUser(@RequestParam("userName") String userName) throws TweetAppException {
		final UserResponseDTO userResponseDTO = userService.getUser(userName);
		if (!userResponseDTO.isSuccess() && userResponseDTO.getErrorMessage() != null
				&& userResponseDTO.getErrorMessage().length() > 0) {
			return new ResponseEntity<>(userResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
	}

	@GetMapping(value = "/users/all")
	public ResponseEntity<UserResponseDTO> getAllUsers() throws TweetAppException {
		final UserResponseDTO userResponseDTO = userService.getAllUsers();
		if (!userResponseDTO.isSuccess() && userResponseDTO.getErrorMessage() != null
				&& userResponseDTO.getErrorMessage().length() > 0) {
			return new ResponseEntity<>(userResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
	}

	@GetMapping(value = "/forgot")
	public ResponseEntity<UserResponseDTO> forgotPassword(@RequestParam("userName") String userName,
			@RequestParam("newPassword") String password) throws TweetAppException {
		final UserResponseDTO userResponseDTO = userService.forgotPassword(userName, password);
		if (!userResponseDTO.isSuccess() && userResponseDTO.getErrorMessage() != null
				&& userResponseDTO.getErrorMessage().length() > 0) {
			return new ResponseEntity<>(userResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
	}

}
