package org.study.learning_mate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.study.learning_mate.SuccessResponse;
import org.study.learning_mate.dto.UserDTO;

@Tag(name = "Login API", description = "Login API")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청에 성공하였습니다.", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "이미 가입된 계정입니다.", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "서버 에러입니다.", content = @Content(mediaType = "application/json"))
})
@RestController
public class LoginController {
    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Login using Spring Security")
    public SuccessResponse<?> login(@RequestBody UserDTO.Login request) {
        // 실제 로직 없이 Swagger 문서화만 목적
        // 이 메서드는 실제로 호출되지 않고, Spring Security에서 로그인 처리
        return SuccessResponse.success("data");
    }
}