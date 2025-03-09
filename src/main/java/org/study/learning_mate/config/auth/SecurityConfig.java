package org.study.learning_mate.config.auth;


import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.study.learning_mate.CustomOAuth2UserService;
import org.study.learning_mate.CustomSuccessHandler;
import org.study.learning_mate.RefreshTokenRepository;
import org.study.learning_mate.UserRepository;
import org.study.learning_mate.config.auth.jwt.JWTFilter;
import org.study.learning_mate.config.auth.jwt.JWTUtil;
import org.study.learning_mate.global.ErrorResponse;
import org.study.learning_mate.global.SecurityGlobalExceptionHandler;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;

    public SecurityConfig(
            AuthenticationConfiguration authenticationConfiguration,
            JWTUtil jwtUtil,
            RefreshTokenRepository refreshTokenRepository,
            CustomOAuth2UserService customOAuth2UserService,
            CustomSuccessHandler customSuccessHandler
    ) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        this.customOAuth2UserService = customOAuth2UserService;
        this.customSuccessHandler = customSuccessHandler;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserRepository userRepository) throws Exception, ErrorResponse {

        http
                .cors((corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Arrays.asList(
                                "https://localhost:4000",
                                "https://d2tlq4x5oaekvl.cloudfront.net",
                                "https://d1530z8p59hoso.cloudfront.net/"
                        ));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);

                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));
                        configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));

                        return configuration;
                    }
                })));
        http
               .csrf((auth) -> auth.disable());
        http
                .formLogin((auth) -> auth.disable());
        http
                .httpBasic((auth) -> auth.disable());

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(HttpMethod.GET, "/login", "/", "/health", "/actuator/health", "/join", "/re-issue", "/error", "/logout",
                                "/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**",
                                "/css/**", "/js/**", "/images/**", "/resources/**", "/static/**",
                                "/platforms", "/lectures", "lectures/{lectureId}", "/demand-lectures", "/posts/{postId}/*", "demand-lectures/{demandLectureId}"
                                )
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/re-issue", "/join")
                        .permitAll()
                        .anyRequest().authenticated()
                );
        http
                .addFilterBefore(new SecurityGlobalExceptionHandler(), UsernamePasswordAuthenticationFilter.class);

        http
                .addFilterBefore(new JWTFilter(jwtUtil, userRepository), LoginFilter.class);
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshTokenRepository), UsernamePasswordAuthenticationFilter.class);
        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshTokenRepository), LogoutFilter.class);

//        http
//            .logout(logout -> logout.
//                    logoutUrl("/login")
//                    .logoutSuccessHandler((request, response, authentication) -> {
//                        response.setStatus(HttpServletResponse.SC_OK);
//                    })
//                    .permitAll()
//            );

        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
