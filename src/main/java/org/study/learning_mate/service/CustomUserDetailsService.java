package org.study.learning_mate.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.study.learning_mate.User;
import org.study.learning_mate.UserRepository;
import org.study.learning_mate.dto.CustomUserDetails;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public UserDetails loadUserByEmail (String email) {

        User user = userRepository.findByEmail(email);

        if (user != null) {
            return new CustomUserDetails(user);
        }

        return null;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException { // todo : delete or customizer
        System.out.println("username test : " + email);
        User user = userRepository.findByEmail(email);
        System.out.println(user);
        if (user != null) {
            return new CustomUserDetails(user);
        }

        return null;
    }

}