package com.tweetapp.tweetappbackend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.tweetapp.tweetappbackend.configuration.KafkaProducerConfig;
import com.tweetapp.tweetappbackend.dto.Tweet;
import com.tweetapp.tweetappbackend.dto.TweetResponseDTO;
import com.tweetapp.tweetappbackend.dto.User;
import com.tweetapp.tweetappbackend.exception.TweetAppException;
import com.tweetapp.tweetappbackend.repository.TweetRepository;
import com.tweetapp.tweetappbackend.repository.UserRepository;
import com.tweetapp.tweetappbackend.request.TweetRequest;
import com.tweetapp.tweetappbackend.service.TweetServiceImpl;
import com.tweetapp.tweetappbackend.utility.TweetAppConstants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class TweetServiceImplTest {

    @InjectMocks
    TweetServiceImpl tweetService;

    @Mock
    UserRepository userRepository;

    @Mock
    TweetRepository tweetRepository;

    @Mock
    MongoOperations mongoOperation;

    @Mock
    KafkaProducerConfig kafkaProducer;

    private List<Tweet> allTweets = new ArrayList<>();
    private Tweet tweet1 = new Tweet();
    private Tweet tweet2 = new Tweet();
    private Tweet tweet3 = new Tweet();
    private TweetRequest tweetRequest = new TweetRequest();
    private User user = new User();

    @BeforeEach
    public void setUp() {

        HashMap<String, Integer> tweetLikesMap = new HashMap<>();
        tweetLikesMap.put("isha@gmail.com", 1);
        List<String> replies = new ArrayList<>();
        replies.add("Reply!");
        HashMap<String, List<String>> tweetReplyMap = new HashMap<>();
        tweetReplyMap.put("isha@gmail.com", replies);

        tweet1.setTweetId(1);
        tweet1.setUserName("isha@gmail.com");
        tweet1.setTweet("Tweet!");
        tweet1.setCreatedDateTime(new Date());
        tweet1.setTweetLikes(tweetLikesMap);
        tweet1.setTweetReplies(tweetReplyMap);
        allTweets.add(tweet1);

        tweet2.setTweetId(2);
        tweet2.setUserName("gauri@gmail.com");
        tweet2.setTweet("Tweet!");
        tweet2.setCreatedDateTime(new Date());
        tweet2.setTweetLikes(tweetLikesMap);
        tweet2.setTweetReplies(tweetReplyMap);
        allTweets.add(tweet2);

        tweet3.setTweetId(3);
        tweet3.setUserName("sam@gmail.com");
        tweet3.setTweet("Tweet!");
        tweet3.setCreatedDateTime(new Date());
        tweet3.setTweetLikes(new HashMap<String, Integer>());
        tweet3.setTweetReplies(new HashMap<String, List<String>>());
        allTweets.add(tweet3);

        tweetRequest.setTweetId(1);
        tweetRequest.setUserName("isha@gmail.com");
        tweetRequest.setTweet("Tweet!");

        user.setUserId(1);
        user.setPassword("abc");
        user.setLastName("Saxena");
        user.setFirstName("Isha");
        user.setEmailId("isha@gmail.com");
    }

    @Test
    public void testPostTweet_Success() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.of(user));
        Mockito.when(tweetRepository.findAll()).thenReturn(allTweets);
        final TweetResponseDTO tweetResponseDTO = tweetService.postTweet("isha@gmail.com", tweetRequest);
        assertEquals(TweetAppConstants.POST_TWEET_SUCCESS, tweetResponseDTO.getResponseMessage());
        assertTrue(tweetResponseDTO.isSuccess());
    }

    @Test
    public void testPostTweet_NotValidUser() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.empty());
        final TweetResponseDTO tweetResponseDTO = tweetService.postTweet("ishasxn@gmail.com", tweetRequest);
        assertEquals(TweetAppConstants.INVALID_USERNAME_TWEET_REQUEST, tweetResponseDTO.getResponseMessage());
        assertTrue(tweetResponseDTO.isSuccess());
    }

    @Test
    public void testPostTweet_NotValidUser1() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.of(user));
        final TweetResponseDTO tweetResponseDTO = tweetService.postTweet("ishasxn@gmail.com", tweetRequest);
        assertEquals(TweetAppConstants.INVALID_USERNAME_TWEET_REQUEST, tweetResponseDTO.getResponseMessage());
        assertTrue(tweetResponseDTO.isSuccess());
    }

    @Test
    public void testPostTweet_throwsException() throws TweetAppException {
        TweetAppException thrown = assertThrows(TweetAppException.class, () -> {
            Mockito.when(userRepository.findByEmailId(anyString()))
                    .thenThrow(new RuntimeException());
            final TweetResponseDTO tweetResponseDTO = tweetService.postTweet("ishasxn@gmail.com", tweetRequest);
            assertFalse(tweetResponseDTO.isSuccess());
            assertNotNull(tweetResponseDTO.getErrorMessage());
        });
    }

    @Test
    public void testGetAllTweets_Success() throws TweetAppException {
        Mockito.when(tweetRepository.findAll()).thenReturn(allTweets);
        final TweetResponseDTO tweetResponseDTO = tweetService.getAllTweets();
        assertEquals(TweetAppConstants.TWEET_RETRIEVE_SUCCESS, tweetResponseDTO.getResponseMessage());
        assertTrue(tweetResponseDTO.isSuccess());
    }

    @Test
    public void testGetAllTweets_NoTweets() throws TweetAppException {
        Mockito.when(tweetRepository.findAll()).thenReturn(new ArrayList<>());
        final TweetResponseDTO tweetResponseDTO = tweetService.getAllTweets();
        assertEquals(TweetAppConstants.NO_TWEET_FOUND, tweetResponseDTO.getResponseMessage());
        assertTrue(tweetResponseDTO.isSuccess());
    }

    @Test
    public void testGetAllTweets_throwsException() throws TweetAppException {
        TweetAppException thrown = assertThrows(TweetAppException.class, () -> {
            Mockito.when(tweetRepository.findAll())
                    .thenThrow(new RuntimeException());
            final TweetResponseDTO tweetResponseDTO = tweetService.getAllTweets();
            assertFalse(tweetResponseDTO.isSuccess());
            assertNotNull(tweetResponseDTO.getErrorMessage());
        });
    }

    @Test
    public void testGetAllUserTweets_Success() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.of(user));
        Mockito.when(tweetRepository.findByUserName(anyString())).thenReturn(allTweets);
        final TweetResponseDTO tweetResponseDTO = tweetService.getAllUserTweets("isha@gmail.com");
        assertEquals(TweetAppConstants.TWEET_RETRIEVE_SUCCESS, tweetResponseDTO.getResponseMessage());
        assertTrue(tweetResponseDTO.isSuccess());
    }

    @Test
    public void testGetAllUserTweets_NoUserTweets() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.of(user));
        Mockito.when(tweetRepository.findByUserName(anyString())).thenReturn(new ArrayList<>());
        final TweetResponseDTO tweetResponseDTO = tweetService.getAllUserTweets("isha@gmail.com");
        assertEquals(TweetAppConstants.NO_TWEET_FOUND, tweetResponseDTO.getResponseMessage());
        assertTrue(tweetResponseDTO.isSuccess());
    }

    @Test
    public void testGetAllUserTweets_InvalidUser() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.empty());
        final TweetResponseDTO tweetResponseDTO = tweetService.getAllUserTweets("isha@gmail.com");
        assertEquals(TweetAppConstants.INVALID_USER, tweetResponseDTO.getResponseMessage());
        assertTrue(tweetResponseDTO.isSuccess());
    }

    @Test
    public void testGetAllUserTweets_throwsException() throws TweetAppException {
        TweetAppException thrown = assertThrows(TweetAppException.class, () -> {
            Mockito.when(userRepository.findByEmailId(anyString()))
                    .thenThrow(new RuntimeException());
            final TweetResponseDTO tweetResponseDTO = tweetService.getAllUserTweets("isha@gmail.com");
            assertFalse(tweetResponseDTO.isSuccess());
            assertNotNull(tweetResponseDTO.getErrorMessage());
        });
    }

    @Test
    public void testUpdateTweet_Success() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.of(user));
        Mockito.when(tweetRepository.findById(anyInt())).thenReturn(Optional.of(tweet1));
        Mockito.when(mongoOperation.findAndModify(any(), any(), any())).thenReturn(tweet1);
        final TweetResponseDTO tweetResponseDTO = tweetService.updateTweet("isha@gmail.com", 1, tweetRequest);
        assertEquals(TweetAppConstants.TWEET_UPDATE_SUCCESS, tweetResponseDTO.getResponseMessage());
        assertTrue(tweetResponseDTO.isSuccess());
    }

    @Test
    public void testUpdateTweet_InvalidUser() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.empty());
        final TweetResponseDTO tweetResponseDTO = tweetService.updateTweet("isha@gmail.com", 1, tweetRequest);
        assertEquals(TweetAppConstants.INVALID_USERNAME_TWEET_REQUEST, tweetResponseDTO.getResponseMessage());
        assertTrue(tweetResponseDTO.isSuccess());
    }

    @Test
    public void testUpdateTweet_InvalidTweetId() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.of(user));
        Mockito.when(tweetRepository.findById(anyInt())).thenReturn(Optional.empty());
        final TweetResponseDTO tweetResponseDTO = tweetService.updateTweet("isha@gmail.com", 1, tweetRequest);
        assertEquals(TweetAppConstants.INVALID_USERNAME_TWEET_REQUEST, tweetResponseDTO.getResponseMessage());
        assertTrue(tweetResponseDTO.isSuccess());
    }

    @Test
    public void testUpdateTweet_throwsException() throws TweetAppException {
        TweetAppException thrown = assertThrows(TweetAppException.class, () -> {
            Mockito.when(userRepository.findByEmailId(anyString()))
                    .thenThrow(new RuntimeException());
            final TweetResponseDTO tweetResponseDTO = tweetService.updateTweet("isha@gmail.com", 1, tweetRequest);
            assertFalse(tweetResponseDTO.isSuccess());
            assertNotNull(tweetResponseDTO.getErrorMessage());
        });
    }

    @Test
    public void testLikeTweet_Success() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.of(user));
        Mockito.when(tweetRepository.findById(anyInt())).thenReturn(Optional.of(tweet1));
        Mockito.when(mongoOperation.findAndModify(any(), any(), any())).thenReturn(tweet1);
        final TweetResponseDTO tweetResponseDTO = tweetService.likeTweet("isha@gmail.com", 1);
        assertEquals(TweetAppConstants.TWEET_LIKE_SUCCESS, tweetResponseDTO.getResponseMessage());
        assertTrue(tweetResponseDTO.isSuccess());
    }

    @Test
    public void testLikeTweet_Success1() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.of(user));
        Mockito.when(tweetRepository.findById(anyInt())).thenReturn(Optional.of(tweet2));
        Mockito.when(mongoOperation.findAndModify(any(), any(), any())).thenReturn(tweet2);
        final TweetResponseDTO tweetResponseDTO = tweetService.likeTweet("gauri@gmail.com", 2);
        assertEquals(TweetAppConstants.TWEET_LIKE_SUCCESS, tweetResponseDTO.getResponseMessage());
        assertTrue(tweetResponseDTO.isSuccess());
    }

    @Test
    public void testLikeTweet_Success2() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.of(user));
        Mockito.when(tweetRepository.findById(anyInt())).thenReturn(Optional.of(tweet3));
        Mockito.when(mongoOperation.findAndModify(any(), any(), any())).thenReturn(tweet3);
        final TweetResponseDTO tweetResponseDTO = tweetService.likeTweet("sam@gmail.com", 3);
        assertEquals(TweetAppConstants.TWEET_LIKE_SUCCESS, tweetResponseDTO.getResponseMessage());
        assertTrue(tweetResponseDTO.isSuccess());
    }

    @Test
    public void testLikeTweet_InvalidUser() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.empty());
        final TweetResponseDTO tweetResponseDTO = tweetService.likeTweet("isha@gmail.com", 1);
        assertEquals(TweetAppConstants.INVALID_USERNAME_TWEET_REQUEST, tweetResponseDTO.getResponseMessage());
        assertTrue(tweetResponseDTO.isSuccess());
    }

    @Test
    public void testLikeTweet_InvalidTweetId() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.of(user));
        Mockito.when(tweetRepository.findById(anyInt())).thenReturn(Optional.empty());
        final TweetResponseDTO tweetResponseDTO = tweetService.likeTweet("isha@gmail.com", 1);
        assertEquals(TweetAppConstants.INVALID_USERNAME_TWEET_REQUEST, tweetResponseDTO.getResponseMessage());
        assertTrue(tweetResponseDTO.isSuccess());
    }

    @Test
    public void testLikeTweet_throwsException() throws TweetAppException {
        TweetAppException thrown = assertThrows(TweetAppException.class, () -> {
            Mockito.when(userRepository.findByEmailId(anyString()))
                    .thenThrow(new RuntimeException());
            final TweetResponseDTO tweetResponseDTO = tweetService.likeTweet("isha@gmail.com", 1);
            assertFalse(tweetResponseDTO.isSuccess());
            assertNotNull(tweetResponseDTO.getErrorMessage());
        });
    }

    @Test
    public void testReplyTweet_Success() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.of(user));
        Mockito.when(tweetRepository.findById(anyInt())).thenReturn(Optional.of(tweet1));
        Mockito.when(mongoOperation.findAndModify(any(), any(), any())).thenReturn(tweet1);
        final TweetResponseDTO tweetResponseDTO = tweetService.replyTweet("isha@gmail.com", 1, "Reply.");
        assertEquals(TweetAppConstants.TWEET_REPLY_SUCCESS, tweetResponseDTO.getResponseMessage());
        assertTrue(tweetResponseDTO.isSuccess());
    }

    @Test
    public void testReplyTweet_Success1() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.of(user));
        Mockito.when(tweetRepository.findById(anyInt())).thenReturn(Optional.of(tweet2));
        Mockito.when(mongoOperation.findAndModify(any(), any(), any())).thenReturn(tweet2);
        final TweetResponseDTO tweetResponseDTO = tweetService.replyTweet("gauri@gmail.com", 2, "Reply.");
        assertEquals(TweetAppConstants.TWEET_REPLY_SUCCESS, tweetResponseDTO.getResponseMessage());
        assertTrue(tweetResponseDTO.isSuccess());
    }

    @Test
    public void testReplyTweet_Success2() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.of(user));
        Mockito.when(tweetRepository.findById(anyInt())).thenReturn(Optional.of(tweet3));
        Mockito.when(mongoOperation.findAndModify(any(), any(), any())).thenReturn(tweet3);
        final TweetResponseDTO tweetResponseDTO = tweetService.replyTweet("sam@gmail.com", 3, "Reply.");
        assertEquals(TweetAppConstants.TWEET_REPLY_SUCCESS, tweetResponseDTO.getResponseMessage());
        assertTrue(tweetResponseDTO.isSuccess());
    }

    @Test
    public void testReplyTweet_InvalidUser() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.empty());
        final TweetResponseDTO tweetResponseDTO = tweetService.replyTweet("isha@gmail.com", 1, "Reply.");
        assertEquals(TweetAppConstants.INVALID_USERNAME_TWEET_REQUEST, tweetResponseDTO.getResponseMessage());
        assertTrue(tweetResponseDTO.isSuccess());
    }

    @Test
    public void testReplyTweet_InvalidTweetId() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.of(user));
        Mockito.when(tweetRepository.findById(anyInt())).thenReturn(Optional.empty());
        final TweetResponseDTO tweetResponseDTO = tweetService.replyTweet("isha@gmail.com", 1, "Reply.");
        assertEquals(TweetAppConstants.INVALID_USERNAME_TWEET_REQUEST, tweetResponseDTO.getResponseMessage());
        assertTrue(tweetResponseDTO.isSuccess());
    }

    @Test
    public void testReplyTweet_throwsException() throws TweetAppException {
        TweetAppException thrown = assertThrows(TweetAppException.class, () -> {
            Mockito.when(userRepository.findByEmailId(anyString()))
                    .thenThrow(new RuntimeException());
            final TweetResponseDTO tweetResponseDTO = tweetService.replyTweet("isha@gmail.com", 1, "Reply.");
            assertFalse(tweetResponseDTO.isSuccess());
            assertNotNull(tweetResponseDTO.getErrorMessage());
        });
    }

    @Test
    public void testDeleteTweet_Success() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.of(user));
        Mockito.when(tweetRepository.findById(anyInt())).thenReturn(Optional.of(tweet1));
        final TweetResponseDTO tweetResponseDTO = tweetService.deleteTweet("isha@gmail.com", 1);
        assertEquals(TweetAppConstants.TWEET_DELETE_SUCCESS, tweetResponseDTO.getResponseMessage());
        assertTrue(tweetResponseDTO.isSuccess());
    }

    @Test
    public void testDeleteTweet_InvalidUser() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.empty());
        final TweetResponseDTO tweetResponseDTO = tweetService.deleteTweet("isha@gmail.com", 1);
        assertEquals(TweetAppConstants.INVALID_USERNAME_TWEET_REQUEST, tweetResponseDTO.getResponseMessage());
        assertTrue(tweetResponseDTO.isSuccess());
    }

    @Test
    public void testDeleteTweet_InvalidTweetId() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.of(user));
        Mockito.when(tweetRepository.findById(anyInt())).thenReturn(Optional.empty());
        final TweetResponseDTO tweetResponseDTO = tweetService.deleteTweet("isha@gmail.com", 1);
        assertEquals(TweetAppConstants.INVALID_USERNAME_TWEET_REQUEST, tweetResponseDTO.getResponseMessage());
        assertTrue(tweetResponseDTO.isSuccess());
    }

    @Test
    public void testDeleteTweet_throwsException() throws TweetAppException {
        TweetAppException thrown = assertThrows(TweetAppException.class, () -> {
            Mockito.when(userRepository.findByEmailId(anyString()))
                    .thenThrow(new RuntimeException());
            final TweetResponseDTO tweetResponseDTO = tweetService.deleteTweet("isha@gmail.com", 1);
            assertFalse(tweetResponseDTO.isSuccess());
            assertNotNull(tweetResponseDTO.getErrorMessage());
        });
    }

}