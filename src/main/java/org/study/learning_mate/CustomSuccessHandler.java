package org.study.learning_mate;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.study.learning_mate.config.auth.jwt.JWTUtil;
import org.study.learning_mate.dto.CustomOAuth2UserDTO;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

@Slf4j
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

    public CustomSuccessHandler(JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("check principal");
        System.out.println("principal: " + authentication.getPrincipal());
        CustomOAuth2UserDTO customUserDetails = (CustomOAuth2UserDTO) authentication.getPrincipal();

        Long userId = customUserDetails.getUserId();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String accessToken = jwtUtil.createJwt("access", userId, role, 60*60*10L);
        String refreshToken = jwtUtil.createJwt("refresh", userId, role, 60*60*10L);

        addRefreshEntity(userId, refreshToken, 86400000L);

        String hostHeader = request.getHeader("Host");
        System.out.println("hostHeader : " + hostHeader);
        // 응답 데이터를 Map으로 만들어 SuceessResponse에 담기
//        Map<String, String> data = new HashMap<>();
//        data.put("accessToken", accessToken);


//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");

        Cookie cookie = createCookie("refresh", refreshToken);
        response.addCookie(cookie);

        // 리다이렉트 URL 설정
        String redirectUrl = "https://prom-art.store/app/redirect";
        response.setStatus(HttpServletResponse.SC_FOUND); // 302 상태 코드
        response.setHeader("Location", redirectUrl);
        for (String headerName : response.getHeaderNames()) {
            log.info("응답 헤더 - {}: {}", headerName, response.getHeaders(headerName));
        }
        // 로그 출력 (선택 사항)
        System.out.println("successful oauth Authentication - Cookie: " + cookie.getName() + " = " + cookie.getValue());

    }

    // todo : capsulation

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(604800);  // 7 days

        cookie.setHttpOnly(true); // Set HttpOnly to prevent JavaScript access
//        cookie.setSecure(true);   // Set Secure to ensure it's only sent over HTTPS
        cookie.setPath("/");      // Set the path for the cookie (optional)

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