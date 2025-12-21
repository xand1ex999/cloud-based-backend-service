package com.example.demo.mapper;

import com.example.demo.dto.RegistrationRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.entity.User;

public class UserMapper {

        public static UserResponse toResponse(User user) {
            UserResponse dto = new UserResponse();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setName(user.getName());
            dto.setRole(user.getRole().name());
            dto.setActive(user.getActive());
            dto.setCreatedAt(user.getCreatedAt());
            return dto;
        }

        public static User toEntity(RegistrationRequest dto) {
            User user = new User();
            user.setEmail(dto.getEmail());
            user.setName(dto.getName());
            return user;
        }

}
