package com.example.shopapp.services.impl;

import com.example.shopapp.DTO.UserDTO;
import com.example.shopapp.model.User;

import java.util.List;

public interface UserServiceImpl {
    User createUser(UserDTO userDTO) throws Exception;
    String login(String phoneNumber, String password) throws Exception;
    User getUserDetailsFromToken(String token) throws Exception;
    User updateUser(Long userId,UserDTO userDTO) throws Exception;
    List<User> getAllUser(boolean active);
}
