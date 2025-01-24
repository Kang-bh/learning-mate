package org.study.learning_mate.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.study.learning_mate.SuccessResponse;

@Tag(name = "Logout API", description = "Logout API")
@RestController
public class LogoutController {
    @PostMapping("/logout")
    @Operation(summary = "User Logout", description = "Logout using Spring Security")
    public SuccessResponse<?> logout() {
        // 실제 로직 없이 Swagger 문서화만 목적
        // 이 메서드는 실제로 호출되지 않고, Spring Security에서 로그인 처리
        return SuccessResponse.success("logout");
    }
}