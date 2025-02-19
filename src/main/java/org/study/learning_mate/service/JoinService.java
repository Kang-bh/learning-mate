package org.study.learning_mate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.study.learning_mate.Role;
import org.study.learning_mate.global.ErrorResponse;
import org.study.learning_mate.global.ErrorType;
import org.study.learning_mate.user.User;
import org.study.learning_mate.UserRepository;
import org.study.learning_mate.dto.UserDTO;
import org.study.learning_mate.utils.UserMapper;

@Slf4j
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

    public UserDTO.Info join(UserDTO.Join joinDTO) throws Exception, ErrorResponse {

        String name = joinDTO.getName();
        String password = joinDTO.getPassword();
        String email = joinDTO.getEmail();

        Boolean isExist = userRepository.existsByEmail(email);

        // 이메일 중복 확인
        if (isExist) {
            log.error("Error when checking email");
            throw new ErrorResponse(ErrorType.INVALID_ARGUMENT, "Email already exists");
        }

        String encryptedPassword = bCryptPasswordEncoder.encode(password);

        User user = User.builder()
                .password(encryptedPassword)
                .name(name)
                .email(email)
                .role(Role.USER)
                .build();

        User userEntity = userRepository.save(user);
        return userMapper.toUserInfo(userEntity);

    }
}