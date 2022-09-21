package com.tweetapp.tweetappbackend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.tweetapp.tweetappbackend.dto.User;
import com.tweetapp.tweetappbackend.dto.UserResponseDTO;
import com.tweetapp.tweetappbackend.exception.TweetAppException;
import com.tweetapp.tweetappbackend.repository.UserRepository;
import com.tweetapp.tweetappbackend.utility.TweetAppConstants;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	MongoOperations mongoOperation;

	// @Autowired
	// private KafkaProducerConfig producer;

	public UserResponseDTO loginUser(final String userName, final String password) throws TweetAppException {
		log.info(TweetAppConstants.IN_REQUEST_LOG + "BEGIN: User Login for - " + userName);
		final UserResponseDTO userResponseDTO = new UserResponseDTO();
		String validUser = "";
		try {
			if (!userName.isEmpty() && !password.isEmpty()) {
				Optional<User> userLogin = userRepository.findByEmailIdAndPassword(userName, password);
				validUser = userLogin.isPresent() ? TweetAppConstants.USER_LOGIN_SUCCESS
						: TweetAppConstants.USER_LOGIN_FAILED;
				userResponseDTO.setSuccess(true);
				userResponseDTO.setResponseMessage(validUser);
				log.info("Successful User Login.");
			} else {
				userResponseDTO.setSuccess(true);
				userResponseDTO.setResponseMessage(TweetAppConstants.INVALID_USERNAME_PASSWORD);
				log.info("Incorrect User Login.");
			}
		} catch (final Exception e) {
			log.error("FAILED: USER LOGIN", e);
			userResponseDTO.setSuccess(false);
			userResponseDTO.setErrorMessage(e.toString());
			throw new TweetAppException(TweetAppConstants.USER_REQUEST_FAILED, TweetAppConstants.USER_ERROR_ID);
		}
		log.info(TweetAppConstants.EXITING_RESPONSE_LOG + "END: User Login");
		return userResponseDTO;
	}

	public UserResponseDTO registerUser(final User user) throws TweetAppException {
		log.info(TweetAppConstants.IN_REQUEST_LOG + "BEGIN : Register User for - " + user.getEmailId());
		final UserResponseDTO userResponseDTO = new UserResponseDTO();
		String responseMessage = "";
		try {
			if (user != null) {
				Optional<User> registerUser = userRepository.findByEmailId(user.getEmailId());
				if (registerUser.isPresent()) {
					responseMessage = TweetAppConstants.USER_NAME_ALREADY_EXISTS;
				} else {
					userRepository.save(user);
					responseMessage = TweetAppConstants.USER_NAME_REGISTERED_SUCCESSFULLY;
				}
				userResponseDTO.setSuccess(true);
				userResponseDTO.setResponseMessage(responseMessage);
				log.info("Successful User Registration.");
			}
		} catch (final Exception e) {
			log.error("FAILED: USER REGISTRATION", e);
			userResponseDTO.setSuccess(false);
			userResponseDTO.setErrorMessage(e.toString());
			throw new TweetAppException(TweetAppConstants.USER_REQUEST_FAILED, TweetAppConstants.USER_ERROR_ID);
		}
		log.info(TweetAppConstants.EXITING_RESPONSE_LOG + "END : Register User");
		return userResponseDTO;
	}

	public UserResponseDTO getUser(final String userName) throws TweetAppException {
		log.info(TweetAppConstants.IN_REQUEST_LOG + "BEGIN : Get User with user name - " + userName);
		final UserResponseDTO userResponseDTO = new UserResponseDTO();
		User userResponse = new User();
		try {
			if (!userName.isEmpty()) {
				Optional<User> userExists = userRepository.findByEmailId(userName);
				if (userExists.isPresent()) {
					userResponse = userExists.get();
					userResponseDTO.setSuccess(true);
					userResponseDTO.setResponseMessage(TweetAppConstants.USER_FOUND_SUCCESS);
					userResponseDTO.getUsers().add(userResponse);
					log.info("User Found Successfully.");
				} else {
					userResponseDTO.setSuccess(true);
					userResponseDTO.setResponseMessage(TweetAppConstants.NO_USER_FOUND);
					userResponseDTO.getUsers().add(userResponse);
					log.info("User Not Found.");
				}
			} else {
				userResponseDTO.setSuccess(true);
				userResponseDTO.setResponseMessage(TweetAppConstants.INVALID_USER);
				log.info("Incorrect User.");
			}
		} catch (final Exception e) {
			log.error("FAILED: GET USER", e);
			userResponseDTO.setSuccess(false);
			userResponseDTO.setErrorMessage(e.toString());
			throw new TweetAppException(TweetAppConstants.USER_REQUEST_FAILED, TweetAppConstants.USER_ERROR_ID);
		}
		log.info(TweetAppConstants.EXITING_RESPONSE_LOG + "END : Get User by user name");
		return userResponseDTO;
	}

	public UserResponseDTO getAllUsers() throws TweetAppException {
		log.info(TweetAppConstants.IN_REQUEST_LOG + "BEGIN : Get All Users");
		final UserResponseDTO userResponseDTO = new UserResponseDTO();
		try {
			List<User> allUsers = userRepository.findAll();
			if (!allUsers.isEmpty()) {
				userResponseDTO.setSuccess(true);
				userResponseDTO.setResponseMessage(TweetAppConstants.USER_LIST_RETRIEVED_SUCCESS);
				userResponseDTO.setUsers(allUsers);
				log.info("Users List Retrieved Successfully.");
			} else {
				userResponseDTO.setSuccess(true);
				userResponseDTO.setResponseMessage(TweetAppConstants.NO_USER_FOUND);
				log.info("No Users Found.");
			}
		} catch (final Exception e) {
			log.error("FAILED: GET ALL USERS", e);
			userResponseDTO.setSuccess(false);
			userResponseDTO.setErrorMessage(e.toString());
			throw new TweetAppException(TweetAppConstants.USER_REQUEST_FAILED, TweetAppConstants.USER_ERROR_ID);
		}
		log.info(TweetAppConstants.EXITING_RESPONSE_LOG + "END : Get All Users");
		return userResponseDTO;
	}

	public UserResponseDTO forgotPassword(final String userName, final String password)
			throws TweetAppException {
		log.info(TweetAppConstants.IN_REQUEST_LOG + "BEGIN : Forgot Password for username - " + userName);
		final UserResponseDTO userResponseDTO = new UserResponseDTO();
		try {
			if (!userName.isEmpty() && !password.isEmpty()) {
				Optional<User> userByEmailIdName = userRepository.findByEmailId(userName);
				if (!userByEmailIdName.isPresent()) {
					userResponseDTO.setSuccess(true);
					userResponseDTO.setResponseMessage(TweetAppConstants.NO_USER_FOUND);
					log.info("User Not Found.");
				}
				// producer.sendMessage("Forgot Password for :: " + userName.concat(" " +
				// password));
				Query query = new Query();
				query.addCriteria(Criteria.where(TweetAppConstants.EMAIL_ID).is(userName));

				Update updatedPassword = new Update();
				updatedPassword.set(TweetAppConstants.PASSWORD, password);

				User user = mongoOperation.findAndModify(query, updatedPassword, User.class);
				if (user != null) {
					userResponseDTO.setSuccess(true);
					userResponseDTO.setResponseMessage(TweetAppConstants.PASSWORD_UPDATED);
					log.info("Password Updated Successfully.");
				}
			} else {
				userResponseDTO.setSuccess(true);
				userResponseDTO.setResponseMessage(TweetAppConstants.INVALID_USERNAME_PASSWORD);
				log.info("Incorrect Password Update.");
			}
		} catch (final Exception e) {
			log.error("FAILED: FORGOT PASSWORD", e);
			userResponseDTO.setSuccess(false);
			userResponseDTO.setErrorMessage(e.toString());
			throw new TweetAppException(TweetAppConstants.USER_REQUEST_FAILED,
					TweetAppConstants.USER_ERROR_ID);
		}
		log.info(TweetAppConstants.EXITING_RESPONSE_LOG + "END : Forgot Password");
		return userResponseDTO;
	}

}
