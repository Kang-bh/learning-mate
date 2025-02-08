package org.study.learning_mate.service;

import org.springframework.stereotype.Service;
import org.study.learning_mate.user.User;
import org.study.learning_mate.UserRepository;
import org.study.learning_mate.dto.UserDTO;
import org.study.learning_mate.utils.UserMapper;

import java.util.NoSuchElementException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper
        ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDTO.Info findUserInfoById(Long userId) {
        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        return userMapper.toUserInfo(userEntity);
    }

    public User findUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NoSuchElementException("unregistered user");
        });

        return user;
    }

}
