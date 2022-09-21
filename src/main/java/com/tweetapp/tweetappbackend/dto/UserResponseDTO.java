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
public class UserResponseDTO {

    List<User> users = new ArrayList<User>();

    private boolean success;

    private String responseMessage;

    private String errorMessage;

    public void addUser(User user) {
        users.add(user);
    }

}
