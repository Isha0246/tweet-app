package com.tweetapp.tweetappbackend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.tweetapp.tweetappbackend.dto.User;
import com.tweetapp.tweetappbackend.dto.UserResponseDTO;
import com.tweetapp.tweetappbackend.exception.TweetAppException;
import com.tweetapp.tweetappbackend.repository.UserRepository;
import com.tweetapp.tweetappbackend.service.UserServiceImpl;
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
public class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    MongoOperations mongoOperation;

    private List<User> allUsers = new ArrayList<>();
    private User user1 = new User();
    private User user2 = new User();

    @BeforeEach
    public void setUp() {

        user1.setUserId(1);
        user1.setPassword("abc");
        user1.setLastName("Saxena");
        user1.setFirstName("Isha");
        user1.setEmailId("ishasxn@gmail.com");
        allUsers.add(user1);

        user2.setUserId(2);
        user2.setPassword("123");
        user2.setLastName("Saxena");
        user2.setFirstName("Anandita");
        user2.setEmailId("anandita@gmail.com");
        allUsers.add(user2);
    }

    @Test
    public void testUserLogin_Success() throws TweetAppException {
        Mockito.when(userRepository.findByEmailIdAndPassword(anyString(), anyString()))
                .thenReturn(Optional.of(new User()));
        final UserResponseDTO loginResponse = userService.loginUser("ishasxn@gmail.com", "123");
        assertEquals(TweetAppConstants.USER_LOGIN_SUCCESS, loginResponse.getResponseMessage());
        assertTrue(loginResponse.isSuccess());
    }

    @Test
    public void testUserLogin_Failed() throws TweetAppException {
        Mockito.when(userRepository.findByEmailIdAndPassword(anyString(), anyString()))
                .thenReturn(Optional.empty());
        final UserResponseDTO loginResponse = userService.loginUser("ishasxn@gmail.com", "123");
        assertEquals(TweetAppConstants.USER_LOGIN_FAILED, loginResponse.getResponseMessage());
        assertTrue(loginResponse.isSuccess());
    }

    @Test
    public void testUserLogin_Failed_IncorrectUsernamePassword() throws TweetAppException {
        final UserResponseDTO loginResponse = userService.loginUser("", "123");
        assertEquals(TweetAppConstants.INVALID_USERNAME_PASSWORD, loginResponse.getResponseMessage());
        assertTrue(loginResponse.isSuccess());
    }

    @Test
    public void testUserLogin_throwsException() throws TweetAppException {
        TweetAppException thrown = assertThrows(TweetAppException.class, () -> {
            Mockito.when(userRepository.findByEmailIdAndPassword(anyString(), anyString()))
                    .thenThrow(new RuntimeException());
            final UserResponseDTO loginResponse = userService.loginUser("ishasxn@gmail.com", "123");
            assertFalse(loginResponse.isSuccess());
            assertNotNull(loginResponse.getErrorMessage());
        });
    }

    @Test
    public void testUserRegistration_Success() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.empty());
        final UserResponseDTO registerResponse = userService.registerUser(user1);
        assertEquals(TweetAppConstants.USER_NAME_REGISTERED_SUCCESSFULLY, registerResponse.getResponseMessage());
        assertTrue(registerResponse.isSuccess());
    }

    @Test
    public void testUserRegistration_UserAlreadyExists() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.of(user1));
        final UserResponseDTO registerResponse = userService.registerUser(user1);
        assertEquals(TweetAppConstants.USER_NAME_ALREADY_EXISTS, registerResponse.getResponseMessage());
        assertTrue(registerResponse.isSuccess());
    }

    @Test
    public void testUserRegistration_throwsException() throws TweetAppException {
        TweetAppException thrown = assertThrows(TweetAppException.class, () -> {
            Mockito.when(userRepository.findByEmailId(anyString()))
                    .thenThrow(new RuntimeException());
            final UserResponseDTO registerResponse = userService.registerUser(user1);
            assertFalse(registerResponse.isSuccess());
            assertNotNull(registerResponse.getErrorMessage());
        });
    }

    @Test
    public void testGetUser_Success() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.of(user2));
        final UserResponseDTO getUser = userService.getUser("anandita@gmail.com");
        assertTrue(getUser.isSuccess());
        assertEquals(TweetAppConstants.USER_FOUND_SUCCESS, getUser.getResponseMessage());
    }

    @Test
    public void testGetUser_NotExists() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.empty());
        final UserResponseDTO getUser = userService.getUser("anandita@gmail.com");
        assertTrue(getUser.isSuccess());
        assertEquals(TweetAppConstants.NO_USER_FOUND, getUser.getResponseMessage());
    }

    @Test
    public void testGetUser_UsernameEmpty() throws TweetAppException {
        final UserResponseDTO getUser = userService.getUser("");
        assertTrue(getUser.isSuccess());
        assertEquals(TweetAppConstants.INVALID_USER, getUser.getResponseMessage());
    }

    @Test
    public void testGetUser_throwsException() throws TweetAppException {
        TweetAppException thrown = assertThrows(TweetAppException.class, () -> {
            Mockito.when(userRepository.findByEmailId(anyString()))
                    .thenThrow(new RuntimeException());
            final UserResponseDTO getUser = userService.getUser("ishasxn@gmail.com");
            assertFalse(getUser.isSuccess());
            assertNotNull(getUser.getErrorMessage());
        });
    }

    @Test
    public void testGetAllUsers_Success() throws TweetAppException {
        Mockito.when(userRepository.findAll()).thenReturn(allUsers);
        final UserResponseDTO allUsers = userService.getAllUsers();
        assertEquals(TweetAppConstants.USER_LIST_RETRIEVED_SUCCESS, allUsers.getResponseMessage());
        assertTrue(allUsers.isSuccess());
    }

    @Test
    public void testGetAllUsers_NoUsers() throws TweetAppException {
        Mockito.when(userRepository.findAll()).thenReturn(new ArrayList<>());
        final UserResponseDTO allUsers = userService.getAllUsers();
        assertEquals(TweetAppConstants.NO_USER_FOUND, allUsers.getResponseMessage());
        assertTrue(allUsers.isSuccess());
    }

    @Test
    public void testGetAllUsers_throwsException() throws TweetAppException {
        TweetAppException thrown = assertThrows(TweetAppException.class, () -> {
            Mockito.when(userRepository.findAll())
                    .thenThrow(new RuntimeException());
            final UserResponseDTO allUsers = userService.getAllUsers();
            assertFalse(allUsers.isSuccess());
            assertNotNull(allUsers.getErrorMessage());
        });
    }

    @Test
    public void testForgotPassword_Success() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.of(user1));
        Mockito.when(mongoOperation.findAndModify(any(), any(), any())).thenReturn(user1);
        final UserResponseDTO forgotPassword = userService.forgotPassword("ishasxn@gmail.com", "123");
        assertEquals(TweetAppConstants.PASSWORD_UPDATED, forgotPassword.getResponseMessage());
        assertTrue(forgotPassword.isSuccess());
    }

    @Test
    public void testForgotPassword_UserNotExists() throws TweetAppException {
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.empty());
        final UserResponseDTO forgotPassword = userService.forgotPassword("ishasxn@gmail.com", "123");
        assertEquals(TweetAppConstants.NO_USER_FOUND, forgotPassword.getResponseMessage());
        assertTrue(forgotPassword.isSuccess());
    }

    @Test
    public void testForgotPassword_EmptyUsernameAndPassword() throws TweetAppException {
        final UserResponseDTO forgotPassword = userService.forgotPassword("", "123");
        assertEquals(TweetAppConstants.INVALID_USERNAME_PASSWORD, forgotPassword.getResponseMessage());
        assertTrue(forgotPassword.isSuccess());
    }

    @Test
    public void testForgotPassword_throwsException() throws TweetAppException {
        TweetAppException thrown = assertThrows(TweetAppException.class, () -> {
            Mockito.when(userRepository.findByEmailId(anyString()))
                    .thenThrow(new RuntimeException());
            final UserResponseDTO forgotPassword = userService.forgotPassword("ishasx@gmail.com", "123");
            assertFalse(forgotPassword.isSuccess());
            assertNotNull(forgotPassword.getErrorMessage());
        });
    }

}
