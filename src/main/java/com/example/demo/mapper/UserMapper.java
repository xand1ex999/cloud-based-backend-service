package com.example.demo.mapper;

import com.example.demo.dto.CreateUserRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.entity.User;

import java.time.LocalDateTime;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setActive(user.getActive());
        return dto;
    }

    public static User toEntity(CreateUserRequest dto, String passwordHash) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordHash);
        user.setCreatedAt(LocalDateTime.now());
        user.setActive(true);
        return user;
    }
}
