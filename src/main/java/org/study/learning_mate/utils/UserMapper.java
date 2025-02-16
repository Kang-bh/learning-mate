package org.study.learning_mate.utils;

import org.springframework.stereotype.Component;
import org.study.learning_mate.user.User;
import org.study.learning_mate.dto.UserDTO;

@Component
public class UserMapper {

    // User
    public UserDTO.Info toUserInfo(User user) {
        return UserDTO.Info.builder()
                .name(user.getName())
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().toString())
                .build();
    }

    public UserDTO.UserProfile toUserProfile(User user) {
        return UserDTO.UserProfile.builder()
                .id(user.getId())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .build();
    }

    public UserDTO.updateUser toUserTempInfo(User user) {
        return UserDTO.updateUser.builder()
                .name(user.getName())
                .email(user.getEmail())
                .backGroundImage(user.getBackgroundImage())
                .profileImage(user.getProfileImage())
                .build();
    }
}