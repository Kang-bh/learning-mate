package org.study.learning_mate.service;

import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(
            UserRepository userRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            UserMapper userMapper
        ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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

    @Transactional
    public UserDTO.updateUser updateUser(Long userId, UserDTO.updateUser info) {
        User user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);

        user.setName(info.getName());
        user.setEmail(info.getEmail());
        user.setBackgroundImage(info.getBackGroundImage());
        user.setProfileImage(info.getProfileImage());

        User savedUser = userRepository.save(user);
        return userMapper.toUserTempInfo(savedUser);
    }

    @Transactional
    public UserDTO.updateUser updateUserProfile(Long userId, String userProfile) {
        User user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);

        user.setProfileImage(userProfile);

        User savedUser = userRepository.save(user);
        return userMapper.toUserTempInfo(savedUser);
    }

    @Transactional
    public UserDTO.updateUser deleteUserProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);
        user.setProfileImage(null);
        User savedUser = userRepository.save(user);
        return userMapper.toUserTempInfo(savedUser);
    }

    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
        return;
    }

    @Transactional
    public void updateUserPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);

        String encryptedPassword = bCryptPasswordEncoder.encode(newPassword);

        user.setPassword(encryptedPassword);

        User savedUser = userRepository.save(user);
        return;
    }

}
