package org.study.learning_mate.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.study.learning_mate.Role;
import org.study.learning_mate.User;
import org.study.learning_mate.UserRepository;
import org.study.learning_mate.dto.UserDTO;
import org.study.learning_mate.utils.UserMapper;

@Service
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserMapper userMapper;

    public JoinService(
            UserRepository userRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userMapper = userMapper;
    }

    public UserDTO.Info join(UserDTO.Join joinDTO) throws Exception {

        String password = joinDTO.getPassword();
        String email = joinDTO.getEmail();

        Boolean isExist = userRepository.existsByEmail(email);

        // 이메일 중복 확인
        if (isExist) {
            throw new Exception("Email already exists");
        }

        String encryptedPassword = bCryptPasswordEncoder.encode(password);

        User user = User.builder()
                .password(encryptedPassword)
                .email(email)
                .role(Role.USER)
                .build();

        User userEntity = userRepository.save(user);
        return userMapper.toUserInfo(userEntity);

    }
}