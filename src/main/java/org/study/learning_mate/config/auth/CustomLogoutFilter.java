package org.study.learning_mate.config.auth;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;
import org.study.learning_mate.RefreshTokenRepository;
import org.study.learning_mate.config.auth.jwt.JWTUtil;

import java.io.IOException;

@Slf4j
public class CustomLogoutFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public CustomLogoutFilter(JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        log.info("requestUri: {}", requestUri);
        if (!requestUri.equals("/logout") || !request.getMethod().equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        log.info("cookies: {}", cookies);
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                    break;
                }
            }
        }

        if (refresh == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Boolean isExist = refreshTokenRepository.existsByRefreshToken(refresh);
        log.info("isExist: {}", isExist);

        if (!isExist) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        refreshTokenRepository.deleteByRefreshToken(refresh);

        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        log.info("do logout");

        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
