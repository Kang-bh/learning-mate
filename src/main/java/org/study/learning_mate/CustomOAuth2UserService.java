package org.study.learning_mate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.study.learning_mate.dto.CustomOAuth2UserDTO;
import org.study.learning_mate.dto.KakaoResponse;
import org.study.learning_mate.dto.OAuth2Response;
import org.study.learning_mate.dto.UserDTO;
import org.study.learning_mate.user.User;

@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    private static final String KAKAO = "kakao";

    public CustomOAuth2UserService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());

        log.info("oAuth2Response : ", oAuth2Response);

        String email = oAuth2Response.getEmail();

        User existData = userRepository.findByEmail(email);

        if (existData == null) {
            log.info("create user");
            User user = User.builder()
//                    .profileImage(oAuth2Response.getProfileImage())
                    .name(oAuth2Response.getName())
                    .email(oAuth2Response.getEmail())
                    .password(null)
                    .role(Role.USER)
                    .build();

            System.out.println("user : " + user);
            User userEntity = userRepository.save(user);

            UserDTO.Info userDTO = UserDTO.Info.builder()
                    .id(userEntity.getId())
                    .role(Role.USER.toString())
                    .email(userEntity.getEmail())
                    .build();

            System.out.println("userDTO : " + userDTO);
            CustomOAuth2UserDTO customUser = new CustomOAuth2UserDTO(userDTO);
            log.info("customUser : " + customUser);
//
            return customUser;
        } else {
            log.info("update user");
            existData.setEmail(oAuth2Response.getEmail());

            User userEntity = userRepository.save(existData);

            UserDTO.Info userDTO = UserDTO.Info.builder()
                    .id(userEntity.getId())
                    .role(userEntity.getRole().toString())
                    .name(userEntity.getName())
                    .email(userEntity.getEmail())
                    .build();

            log.info("userDTO : " + userDTO);
            CustomOAuth2UserDTO customUser = new CustomOAuth2UserDTO(userDTO);
            log.info("customUser : " + customUser);
            return customUser;
        }

    }
}