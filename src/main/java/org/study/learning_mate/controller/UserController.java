package org.study.learning_mate.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.study.learning_mate.SuccessResponse;
import org.study.learning_mate.dto.CustomUserDetails;
import org.study.learning_mate.dto.UserDTO;
import org.study.learning_mate.service.UserService;

@Tag(name = "User API", description = "User 관련 API")
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @Operation(summary = "유저정보 조회", description = "유저 식별값을 통해 유저 정보를 조회합니다.")
    @Parameters({
            @Parameter(in = ParameterIn.HEADER, name = "Authorization", required = true),
            @Parameter(name = "userId", description = "유저 식별값")
    })
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "성공",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = SuccessResponse.class
//                            ))),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorResponse.class)))
//    })
    @GetMapping("/{userId}")
    public SuccessResponse<UserDTO.Info> findUserById(@PathVariable("userId") Long userId) {
        UserDTO.Info result = userService.findUserInfoById(userId);

        return SuccessResponse.success(result);
    }

    @PutMapping("/my")
    public SuccessResponse<UserDTO.updateUser> updateUserInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody UserDTO.updateUser info
    ) {
        UserDTO.updateUser user = userService.updateUser(customUserDetails.getId(), info);
        return SuccessResponse.success(user);
    }

    @GetMapping("/my")
    public SuccessResponse<UserDTO.Info> findMyUserInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        UserDTO.Info result = userService.findUserInfoById(customUserDetails.getId());
        return SuccessResponse.success(result);
    }

    @DeleteMapping("/my")
    public SuccessResponse deleteUser(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        userService.deleteUserById(customUserDetails.getId());
        return SuccessResponse.success(204, "DELETED");
    }
}
