package org.study.learning_mate.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2UserDTO implements OAuth2User {

    private UserDTO.Info userDTO;

    public CustomOAuth2UserDTO(UserDTO.Info userDTO) {
        this.userDTO = userDTO;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return userDTO.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getName() {
        return userDTO.getUsername();
    }

    public String getEmail() {
        return userDTO.getEmail();
    }

    public Long getUserId() {
        return userDTO.getId();
    }
}