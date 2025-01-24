package org.study.learning_mate.config.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;
import org.study.learning_mate.RefreshToken;
import org.study.learning_mate.RefreshTokenRepository;
import org.study.learning_mate.SuccessResponse;
import org.study.learning_mate.config.auth.jwt.JWTUtil;
import org.study.learning_mate.dto.CustomUserDetails;
import org.study.learning_mate.dto.UserDTO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private ObjectMapper objectMapper = new ObjectMapper();
    private RefreshTokenRepository refreshTokenRepository;
    private static final Long accessTokenExpireTime = 86400000L;
    private static final Long refreshTokenExpireTime = 864000000L;

    public LoginFilter(
            AuthenticationManager authenticationManager,
            JWTUtil jwtUtil,
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // Extract username & password from client request
        try {
            log.info("attemptAuthentication");
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

            UserDTO.Join data = objectMapper.readValue(messageBody, UserDTO.Join.class);
            String userId = data.getUserId();
            String password = data.getPassword();
            log.info("attemptAuthentication2");
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userId, password); // username, password 검증을 위해 token 담기
            log.info("attemptAuthentication3");
            return authenticationManager.authenticate(authToken); // AuthenticationManager 전달

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (AuthenticationException e) {
            log.error("Authentication failed: ", e);
            throw e;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();
        log.info("successfulAuthentication1");

        Long userId = customUserDetails.getId();

        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        log.info("successfulAuthentication2");
        String role = auth.getAuthority();
        System.out.println("role " + role);

        String accessToken = jwtUtil.createJwt("access", userId, role, accessTokenExpireTime);
        String refreshToken = jwtUtil.createJwt("refresh", userId, role, refreshTokenExpireTime);

        log.info("successfulAuthentication3");
        addRefreshEntity(userId, refreshToken, refreshTokenExpireTime);
        // 응답 데이터를 Map으로 만들어 SuceessResponse에 담기
        Map<String, String> data = new HashMap<>();
        data.put("accessToken", accessToken);


        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Cookie cookie = createCookie("refresh", refreshToken);
        response.addCookie(cookie);
        // 모든 응답 헤더 출력
        for (String headerName : response.getHeaderNames()) {
            log.info("응답 헤더 - {}: {}", headerName, response.getHeaders(headerName));
        }
        // SuceessResponse로 성공 응답 생성
        SuccessResponse<Map<String, String>> baseResponse = SuccessResponse.success(data);

        // JSON 응답 전송
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), baseResponse);



        System.out.println("successfulAuthentication - Cookie: " + cookie.getName() + " = " + cookie.getValue());

        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(401);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(604800);  // 7 days

        cookie.setHttpOnly(true); // Set HttpOnly to prevent JavaScript access
//        cookie.setSecure(true); // 로컬에서 HTTPS를 사용하지 않을 경우
//        cookie.setPath("/");

        return cookie;
    }

    private void addRefreshEntity(Long userId, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(userId);
        refreshToken.setRefreshToken(refresh);
        refreshToken.setExpiration(date.toString());

        refreshTokenRepository.save(refreshToken);
    }
}
