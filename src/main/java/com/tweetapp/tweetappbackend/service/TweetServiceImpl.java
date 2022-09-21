package com.tweetapp.tweetappbackend.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.tweetapp.tweetappbackend.configuration.KafkaProducerConfig;
import com.tweetapp.tweetappbackend.dto.Tweet;
import com.tweetapp.tweetappbackend.dto.TweetResponseDTO;
import com.tweetapp.tweetappbackend.dto.User;
import com.tweetapp.tweetappbackend.exception.TweetAppException;
import com.tweetapp.tweetappbackend.repository.TweetRepository;
import com.tweetapp.tweetappbackend.repository.UserRepository;
import com.tweetapp.tweetappbackend.request.TweetRequest;
import com.tweetapp.tweetappbackend.utility.TweetAppConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TweetServiceImpl implements TweetService {

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private KafkaProducerConfig kafkaProducer;

    @Override
    public TweetResponseDTO postTweet(String userName, TweetRequest request) throws TweetAppException {
        log.info(TweetAppConstants.IN_REQUEST_LOG + "BEGIN : Post Tweet");
        final TweetResponseDTO tweetResponseDTO = new TweetResponseDTO();
        try {
            if (isValidUser(userName) && request != null && !request.getUserName().isEmpty()
                    && userName.equals(request.getUserName())) {
                List<Tweet> tweetList = tweetRepository.findAll();
                if (tweetList != null) {
                    Collections.sort(tweetList, new Comparator<Tweet>() {
                        public int compare(Tweet t1, Tweet t2) {
                            return t2.getCreatedDateTime().compareTo(t1.getCreatedDateTime());
                        }
                    });
                    long tweetIdCount = 1;
                    if (tweetList.size() > 0) {
                        tweetIdCount = tweetList.get(0).getTweetId() + 1;
                    }

                    final Tweet tweet = new Tweet(((int) tweetIdCount), request.getUserName(), request.getTweet(),
                            new Date(System.currentTimeMillis()), new HashMap<String, Integer>(),
                            new HashMap<String, List<String>>());
                    tweetRepository.save(tweet);
                    // kafkaProducer.sendMessage("Tweet Added with tweetId" + tweet.getTweetId());
                    tweetResponseDTO.setSuccess(true);
                    tweetResponseDTO.setResponseMessage(TweetAppConstants.POST_TWEET_SUCCESS);
                    log.info("Tweet Posted Successfully.");
                }
            } else {
                tweetResponseDTO.setSuccess(true);
                tweetResponseDTO.setResponseMessage(TweetAppConstants.INVALID_USERNAME_TWEET_REQUEST);
                log.info("Invalid Username/Tweet Request.");
            }
        } catch (final Exception e) {
            log.error("FAILED: POST TEET", e);
            tweetResponseDTO.setSuccess(false);
            tweetResponseDTO.setErrorMessage(e.toString());
            throw new TweetAppException(TweetAppConstants.TWEET_REQUEST_FAILED, TweetAppConstants.TWEET_ERROR_ID);
        }
        log.info(TweetAppConstants.EXITING_RESPONSE_LOG + "END: Post Tweet");
        return tweetResponseDTO;
    }

    @Override
    public TweetResponseDTO getAllTweets() throws TweetAppException {
        log.info(TweetAppConstants.IN_REQUEST_LOG + "BEGIN : Get All Tweets");
        final TweetResponseDTO tweetResponseDTO = new TweetResponseDTO();
        try {
            List<Tweet> tweetList = tweetRepository.findAll();
            if (tweetList != null && !CollectionUtils.isEmpty(tweetList)) {
                Collections.sort(tweetList, new Comparator<Tweet>() {
                    public int compare(Tweet t1, Tweet t2) {
                        return t2.getCreatedDateTime().compareTo(t1.getCreatedDateTime());
                    }
                });
                tweetResponseDTO.setSuccess(true);
                tweetResponseDTO.setResponseMessage(TweetAppConstants.TWEET_RETRIEVE_SUCCESS);
                tweetResponseDTO.setTweets(tweetList);
                log.info("All Tweets Retrieved Successfully.");
            } else {
                tweetResponseDTO.setSuccess(true);
                tweetResponseDTO.setResponseMessage(TweetAppConstants.NO_TWEET_FOUND);
                log.info("No Tweets Found.");
            }
        } catch (final Exception e) {
            log.error("FAILED: GET ALL TWEETS", e);
            tweetResponseDTO.setSuccess(false);
            tweetResponseDTO.setErrorMessage(e.toString());
            throw new TweetAppException(TweetAppConstants.TWEET_REQUEST_FAILED, TweetAppConstants.TWEET_ERROR_ID);
        }
        log.info(TweetAppConstants.EXITING_RESPONSE_LOG + "END : Get All Tweets");
        return tweetResponseDTO;
    }

    @Override
    public TweetResponseDTO getAllUserTweets(final String userName) throws TweetAppException {
        log.info(TweetAppConstants.IN_REQUEST_LOG + "BEGIN : Get All Tweeets for User - " + userName);
        final TweetResponseDTO tweetResponseDTO = new TweetResponseDTO();
        try {
            if (isValidUser(userName)) {
                final List<Tweet> userTweetList = tweetRepository.findByUserName(userName);
                if (userTweetList != null && !CollectionUtils.isEmpty(userTweetList)) {
                    Collections.sort(userTweetList, new Comparator<Tweet>() {
                        public int compare(Tweet t1, Tweet t2) {
                            return t2.getCreatedDateTime().compareTo(t1.getCreatedDateTime());
                        }
                    });
                    tweetResponseDTO.setSuccess(true);
                    tweetResponseDTO.setTweets(userTweetList);
                    tweetResponseDTO.setResponseMessage(TweetAppConstants.TWEET_RETRIEVE_SUCCESS);
                    log.info("User Tweets Retrieved Successfully.");
                } else {
                    tweetResponseDTO.setSuccess(true);
                    tweetResponseDTO.setResponseMessage(TweetAppConstants.NO_TWEET_FOUND);
                    log.info("No Tweets Found for User.");
                }
            } else {
                tweetResponseDTO.setSuccess(true);
                tweetResponseDTO.setResponseMessage(TweetAppConstants.INVALID_USER);
                log.info("Invalid username.");
            }
        } catch (final Exception e) {
            log.error("FAILED: GET ALL USER TWEETS", e);
            tweetResponseDTO.setSuccess(false);
            tweetResponseDTO.setErrorMessage(e.toString());
            throw new TweetAppException(TweetAppConstants.TWEET_REQUEST_FAILED, TweetAppConstants.TWEET_ERROR_ID);
        }
        log.info(TweetAppConstants.EXITING_RESPONSE_LOG + "END : Get All Tweets for User");
        return tweetResponseDTO;
    }

    @Override
    public TweetResponseDTO updateTweet(String userName, int tweetId, TweetRequest request)
            throws TweetAppException {
        log.info(TweetAppConstants.IN_REQUEST_LOG + "BEGIN : Update Tweet");
        final TweetResponseDTO tweetResponseDTO = new TweetResponseDTO();
        try {
            if (isValidUser(userName) && isValidTweet(tweetId) && !request.getUserName().isEmpty()
                    && userName.equals(request.getUserName())) {
                Optional<Tweet> tweetById = tweetRepository.findById(tweetId);
                if (tweetById.isPresent()) {
                    Tweet tweet = tweetById.get();
                    Query query = new Query();
                    query.addCriteria(Criteria.where(TweetAppConstants.TWEET_ID).is(tweetId));
                    Update update = new Update();
                    update.set(TweetAppConstants.TWEET, request.getTweet());

                    tweet = mongoOperations.findAndModify(query, update, Tweet.class);
                    if (tweet != null) {
                        tweetResponseDTO.setSuccess(true);
                        tweetResponseDTO.setResponseMessage(TweetAppConstants.TWEET_UPDATE_SUCCESS);
                        log.info("Tweet Updated Successfully.");
                    }
                }
            } else {
                tweetResponseDTO.setSuccess(true);
                tweetResponseDTO.setResponseMessage(TweetAppConstants.INVALID_USERNAME_TWEET_REQUEST);
                log.info("Invalid Username/TweetID.");
            }
        } catch (final Exception e) {
            log.warn("FAILED: UPDATE TWEET");
            tweetResponseDTO.setSuccess(false);
            tweetResponseDTO.setErrorMessage(e.toString());
            throw new TweetAppException(TweetAppConstants.TWEET_REQUEST_FAILED, TweetAppConstants.TWEET_ERROR_ID);
        }
        log.info(TweetAppConstants.EXITING_RESPONSE_LOG + "END: Update Tweet");
        return tweetResponseDTO;
    }

    @Override
    public TweetResponseDTO likeTweet(String userName, int tweetId) throws TweetAppException {
        log.info(TweetAppConstants.IN_REQUEST_LOG + "BEGIN : Like Tweet");
        final TweetResponseDTO tweetResponseDTO = new TweetResponseDTO();
        try {
            if (isValidUser(userName) && isValidTweet(tweetId)) {
                Optional<Tweet> tweetByID = tweetRepository.findById(tweetId);
                if (tweetByID.isPresent()) {
                    Tweet tweet = tweetByID.get();
                    final Map<String, Integer> tweetLikesMap = tweet.getTweetLikes();
                    if (tweetLikesMap != null) {
                        tweetLikesMap.put(userName, 1);
                        tweet.setTweetLikes(tweetLikesMap);
                    } else {
                        final Map<String, Integer> newTweetLikesMap = new HashMap<>();
                        newTweetLikesMap.put(userName, 1);
                        tweet.setTweetLikes(newTweetLikesMap);
                    }
                    Query query = new Query();
                    query.addCriteria(Criteria.where(TweetAppConstants.TWEET_ID).is(tweetId));
                    Update update = new Update();
                    update.set(TweetAppConstants.LIKES, tweet.getTweetLikes());
                    tweet = mongoOperations.findAndModify(query, update, Tweet.class);
                    if (tweet != null) {
                        tweetResponseDTO.setSuccess(true);
                        tweetResponseDTO.setResponseMessage(TweetAppConstants.TWEET_LIKE_SUCCESS);
                        log.info("Tweet Liked.");
                    }
                }
            } else {
                tweetResponseDTO.setSuccess(true);
                tweetResponseDTO.setResponseMessage(TweetAppConstants.INVALID_USERNAME_TWEET_REQUEST);
                log.info("Invalid Username/TweetID.");
            }
        } catch (final Exception e) {
            log.error("FAILED: LIKE TWEET", e);
            tweetResponseDTO.setSuccess(false);
            tweetResponseDTO.setErrorMessage(e.toString());
            throw new TweetAppException(TweetAppConstants.TWEET_REQUEST_FAILED, TweetAppConstants.TWEET_ERROR_ID);
        }
        log.info(TweetAppConstants.EXITING_RESPONSE_LOG + "END : Like Tweet");
        return tweetResponseDTO;
    }

    @Override
    public TweetResponseDTO replyTweet(String userName, int tweetId, String reply) throws TweetAppException {
        log.info(TweetAppConstants.IN_REQUEST_LOG + "BEGIN : Reply Tweet");
        final TweetResponseDTO tweetResponseDTO = new TweetResponseDTO();
        try {
            if (isValidUser(userName) && isValidTweet(tweetId)) {
                Optional<Tweet> tweetById = tweetRepository.findById(tweetId);
                if (tweetById.isPresent()) {
                    Tweet tweet = tweetById.get();
                    Map<String, List<String>> tweetReplyMap = tweet.getTweetReplies();
                    if (tweetReplyMap != null) {
                        if (tweetReplyMap.containsKey(userName)) {
                            tweetReplyMap.get(userName).add(reply);
                            tweet.setTweetReplies(tweetReplyMap);
                        } else {
                            tweetReplyMap.put(userName, Arrays.asList(reply));
                            tweet.setTweetReplies(tweetReplyMap);
                        }
                    } else {
                        final Map<String, List<String>> newTweetReplyMap = new HashMap<>();
                        newTweetReplyMap.put(userName, Arrays.asList(reply));
                        tweet.setTweetReplies(newTweetReplyMap);
                    }
                    Query query = new Query();
                    query.addCriteria(Criteria.where(TweetAppConstants.TWEET_ID).is(tweetId));
                    Update update = new Update();
                    update.set(TweetAppConstants.REPLIES, tweet.getTweetReplies());

                    tweet = mongoOperations.findAndModify(query, update, Tweet.class);
                    if (tweet != null) {
                        tweetResponseDTO.setSuccess(true);
                        tweetResponseDTO.setResponseMessage(TweetAppConstants.TWEET_REPLY_SUCCESS);
                        log.info("Replied To Tweeet Successfully.");
                    }
                }
            } else {
                tweetResponseDTO.setSuccess(true);
                tweetResponseDTO.setResponseMessage(TweetAppConstants.INVALID_USERNAME_TWEET_REQUEST);
                log.info("Invalid Username/TweetID.");
            }
        } catch (final Exception e) {
            log.error("FAILED: REPLY TWEET", e);
            tweetResponseDTO.setSuccess(false);
            tweetResponseDTO.setErrorMessage(e.toString());
            throw new TweetAppException(TweetAppConstants.TWEET_REQUEST_FAILED, TweetAppConstants.TWEET_ERROR_ID);
        }
        log.info(TweetAppConstants.EXITING_RESPONSE_LOG + "END: Reply Tweet");
        return tweetResponseDTO;
    }

    @Override
    public TweetResponseDTO deleteTweet(final String userName, final int tweetId) throws TweetAppException {
        log.info(TweetAppConstants.IN_REQUEST_LOG + "BEGIN : Deleting Tweet with tweet ID - " + tweetId);
        final TweetResponseDTO tweetResponseDTO = new TweetResponseDTO();
        try {
            if (isValidUser(userName) && isValidTweet(tweetId)) {
                tweetRepository.deleteById(tweetId);
                // kafkaProducer.sendMessage("Tweet Deleted with tweetId : " + tweetId);
                tweetResponseDTO.setSuccess(true);
                tweetResponseDTO.setResponseMessage(TweetAppConstants.TWEET_DELETE_SUCCESS);
                log.info("Tweet Deleted Successfully.");
            } else {
                tweetResponseDTO.setSuccess(true);
                tweetResponseDTO.setResponseMessage(TweetAppConstants.INVALID_USERNAME_TWEET_REQUEST);
                log.info("Invalid Username/TweetID.");
            }
        } catch (final Exception e) {
            log.error("FAILED: DELETE TWEET", e);
            tweetResponseDTO.setSuccess(false);
            tweetResponseDTO.setErrorMessage(e.toString());
            throw new TweetAppException(TweetAppConstants.TWEET_REQUEST_FAILED, TweetAppConstants.TWEET_ERROR_ID);
        }
        log.info(TweetAppConstants.EXITING_RESPONSE_LOG + "END : Deleting Tweet for User");
        return tweetResponseDTO;
    }

    private boolean isValidUser(final String username) {
        if (!username.isEmpty()) {
            Optional<User> user = userRepository.findByEmailId(username);
            if (user.isPresent()) {
                log.info("User with username - " + username + "is Valid");
                return true;
            }
        }
        log.info("User with username - " + username + "is Invalid");
        return false;
    }

    private boolean isValidTweet(final int tweetId) {
        if (tweetId > 0) {
            Optional<Tweet> tweet = tweetRepository.findById(tweetId);
            if (tweet.isPresent()) {
                log.info("Tweet with tweet ID - " + tweetId + "is Valid");
                return true;
            }
        }
        log.info("Tweet with tweet ID - " + tweetId + "is Invalid");
        return false;
    }

}
