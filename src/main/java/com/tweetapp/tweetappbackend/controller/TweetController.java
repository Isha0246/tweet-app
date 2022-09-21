package com.tweetapp.tweetappbackend.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.tweetappbackend.dto.Tweet;
import com.tweetapp.tweetappbackend.dto.TweetResponseDTO;
import com.tweetapp.tweetappbackend.exception.TweetAppException;
import com.tweetapp.tweetappbackend.request.TweetRequest;
import com.tweetapp.tweetappbackend.service.TweetService;
import org.springframework.web.bind.annotation.CrossOrigin;

import lombok.Generated;

@RequestMapping(value = "/api/v1.0/tweets")
@RestController
@Generated
@CrossOrigin(origins= "http://localhost:4200")
public class TweetController {

	@Autowired
	TweetService tweetService;

	@PostMapping("/add/{userName}")
	public ResponseEntity<TweetResponseDTO> postTweet(@PathVariable("userName") String userName,
			@RequestBody @Valid TweetRequest tweet) throws TweetAppException {
		final TweetResponseDTO tweetResponseDTO = tweetService.postTweet(userName, tweet);
		if (!tweetResponseDTO.isSuccess() && tweetResponseDTO.getErrorMessage() != null
				&& tweetResponseDTO.getErrorMessage().length() > 0) {
			return new ResponseEntity<>(tweetResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(tweetResponseDTO, HttpStatus.OK);
	}

	@GetMapping("/all")
	public ResponseEntity<TweetResponseDTO> getAllTweets() throws TweetAppException {
		final TweetResponseDTO tweetResponseDTO = tweetService.getAllTweets();
		if (!tweetResponseDTO.isSuccess() && tweetResponseDTO.getErrorMessage() != null
				&& tweetResponseDTO.getErrorMessage().length() > 0) {
			return new ResponseEntity<>(tweetResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(tweetResponseDTO, HttpStatus.OK);
	}

	@GetMapping("/{userName}")
	public ResponseEntity<TweetResponseDTO> getAllUserTweets(@PathVariable String userName) throws TweetAppException {
		final TweetResponseDTO tweetResponseDTO = tweetService.getAllUserTweets(userName);
		if (!tweetResponseDTO.isSuccess() && tweetResponseDTO.getErrorMessage() != null
				&& tweetResponseDTO.getErrorMessage().length() > 0) {
			return new ResponseEntity<>(tweetResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(tweetResponseDTO, HttpStatus.OK);
	}

	@PutMapping("/{userName}/update/{tweetId}")
	public ResponseEntity<TweetResponseDTO> updateTweet(@PathVariable("userName") String userName,
			@PathVariable("tweetId") int tweetId, @RequestBody @Valid TweetRequest tweetRequest)
			throws TweetAppException {
		final TweetResponseDTO tweetResponseDTO = tweetService.updateTweet(userName, tweetId, tweetRequest);
		if (!tweetResponseDTO.isSuccess() && tweetResponseDTO.getErrorMessage() != null
				&& tweetResponseDTO.getErrorMessage().length() > 0) {
			return new ResponseEntity<>(tweetResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(tweetResponseDTO, HttpStatus.OK);
	}

	@PutMapping("/{userName}/like/{tweetId}")
	public ResponseEntity<TweetResponseDTO> likeTweet(@PathVariable("userName") String userName,
			@PathVariable("tweetId") int tweetId) throws TweetAppException {
		final TweetResponseDTO tweetResponseDTO = tweetService.likeTweet(userName, tweetId);
		if (!tweetResponseDTO.isSuccess() && tweetResponseDTO.getErrorMessage() != null
				&& tweetResponseDTO.getErrorMessage().length() > 0) {
			return new ResponseEntity<>(tweetResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(tweetResponseDTO, HttpStatus.OK);
	}

	@PostMapping("/{userName}/reply/{tweetId}")
	public ResponseEntity<TweetResponseDTO> replyTweet(@PathVariable("userName") String userName,
			@PathVariable("tweetId") int tweetId, @RequestBody @Valid String reply) throws TweetAppException {
		final TweetResponseDTO tweetResponseDTO = tweetService.replyTweet(userName, tweetId, reply);
		if (!tweetResponseDTO.isSuccess() && tweetResponseDTO.getErrorMessage() != null
				&& tweetResponseDTO.getErrorMessage().length() > 0) {
			return new ResponseEntity<>(tweetResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(tweetResponseDTO, HttpStatus.OK);
	}

	@DeleteMapping("/{userName}/delete/{tweetId}")
	public ResponseEntity<TweetResponseDTO> deleteTweet(@PathVariable("userName") String userName,
			@PathVariable("tweetId") int tweetId) throws TweetAppException {
		final TweetResponseDTO tweetResponseDTO = tweetService.deleteTweet(userName, tweetId);
		if (!tweetResponseDTO.isSuccess() && tweetResponseDTO.getErrorMessage() != null
				&& tweetResponseDTO.getErrorMessage().length() > 0) {
			return new ResponseEntity<>(tweetResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(tweetResponseDTO, HttpStatus.OK);
	}

}
