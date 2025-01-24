package org.study.learning_mate.utils;

import org.springframework.stereotype.Component;
import org.study.learning_mate.User;
import org.study.learning_mate.dto.UserDTO;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    // User
    public UserDTO.Info toUserInfo(User user) {
        return UserDTO.Info.builder()
                .username(user.getUsername())
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().toString())
                .build();
    }
}