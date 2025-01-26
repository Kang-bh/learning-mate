package org.study.learning_mate.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.study.learning_mate.RefreshToken;
import org.study.learning_mate.RefreshTokenRepository;
import org.study.learning_mate.SuccessResponse;
import org.study.learning_mate.config.auth.jwt.JWTUtil;
import org.study.learning_mate.global.ErrorResponse;

import java.util.Date;

import static org.study.learning_mate.global.ErrorType.INVALID_INPUT_VALUE;

@Slf4j
@Tag(name = "Reissue API", description = "Reissue API")
@RestController
@ResponseBody
public class ReissueController {

    private static final Long accessTokenExpireTime = 86400000L;
    private static final Long refreshTokenExpireTime = 864000000L;
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public ReissueController(JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @PostMapping("/re-issue")
    public SuccessResponse<?> reissue(HttpServletRequest request, HttpServletResponse response) throws ErrorResponse {
        log.info("Reissue request received");
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        log.info("Reissue request cookies");
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                log.info("Reissue request refresh" + cookie.getValue());
                refresh = cookie.getValue();
            }
        }
        log.info("Reissue refresh token: {}", refresh);
        if (refresh == null) {
            throw new ErrorResponse(INVALID_INPUT_VALUE, "no refresh token");
        }


        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            throw new ErrorResponse(INVALID_INPUT_VALUE, "refresh token expired");
        }

        String category = jwtUtil.getCategory(refresh);
        log.info("Reissue category: {}", category);

        if (!category.equals("refresh")) {
            throw new ErrorResponse(INVALID_INPUT_VALUE, "refresh token is invalid");
        }

        Long userId = jwtUtil.getUserId(refresh);
        log.info("Reissue user:", userId);
        String role = jwtUtil.getRole(refresh);

        String newAccess = jwtUtil.createJwt("access", userId, role, accessTokenExpireTime);
        String newRefresh = jwtUtil.createJwt("refresh", userId, role, refreshTokenExpireTime);

        refreshTokenRepository.deleteByRefreshToken(refresh);
        addRefreshEntity(userId, newRefresh, refreshTokenExpireTime);

        Cookie cookie = createCookie("refresh", newRefresh);
//        String cookieHeader = String.format(
//                "%s=%s; Max-Age=%d; Path=%s; HttpOnly; Secure; SameSite=None",
//                cookie.getName(), cookie.getValue(), cookie.getMaxAge(), cookie.getPath()
//        );

        response.setHeader("Set-Cookie", String.valueOf(cookie));
        response.addCookie(cookie);

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("accessToken", newAccess);

        return SuccessResponse.success(jsonResponse); // todo : add response record
    }


    //todo :capsulation
    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(604800);  // 7 days

        cookie.setHttpOnly(true); // Set HttpOnly to prevent JavaScript access
//        cookie.setSecure(true);   // Set Secure to ensure it's only sent over HTTPS
//        cookie.setPath("/");      // Set the path for the cookie (optional)

        return cookie;
    }

    private void addRefreshEntity(Long userId, String newRefresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshToken refreshEntity = new RefreshToken();
        refreshEntity.setUserId(userId);
        refreshEntity.setRefreshToken(newRefresh);
        refreshEntity.setExpiration(date.toString());

        refreshTokenRepository.save(refreshEntity);

    }
}
