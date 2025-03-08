package org.study.learning_mate.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.study.learning_mate.SuccessResponse;
import org.study.learning_mate.dto.CustomUserDetails;
import org.study.learning_mate.dto.UserDTO;
import org.study.learning_mate.service.S3Service;
import org.study.learning_mate.service.UserService;

import java.io.IOException;

@Tag(name = "User API", description = "User 관련 API")
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final S3Service s3Service;

    public UserController(
            UserService userService,
            S3Service s3Service
    ) {
        this.userService = userService;
        this.s3Service = s3Service;
    }

    @Operation(summary = "유저정보 조회", description = "유저 식별값을 통해 유저 정보를 조회합니다.")
    @Parameters({
            @Parameter(in = ParameterIn.HEADER, name = "Authorization", required = true),
            @Parameter(name = "userId", description = "유저 식별값")
    })
    @GetMapping("/{userId}")
    public SuccessResponse<UserDTO.Info> findUserById(@PathVariable("userId") Long userId) {
        UserDTO.Info result = userService.findUserInfoById(userId);

        return SuccessResponse.success(result);
    }

    @Operation(summary = "내 정보 수정", description = "유저 식별값을 통해 유저 정보를 조회합니다.")
    @Parameters({
            @Parameter(in = ParameterIn.HEADER, name = "Authorization", required = true),
    })
    @PutMapping("/my")
    public SuccessResponse<UserDTO.updateUser> updateUserInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User 갱신 객체",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.updateUser.class)
                    )
            )
            @RequestBody UserDTO.updateUser info
    ) {
        UserDTO.updateUser user = userService.updateUser(customUserDetails.getId(), info);
        return SuccessResponse.success(user);
    }

    @Operation(summary = "유저정보 조회", description = "유저 식별값을 통해 유저 정보를 조회합니다.")
    @Parameters({
            @Parameter(in = ParameterIn.HEADER, name = "Authorization", required = true),
            @Parameter(name = "userId", description = "유저 식별값")
    })
    @GetMapping("/my")
    public SuccessResponse<UserDTO.Info> findMyUserInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        UserDTO.Info result = userService.findUserInfoById(customUserDetails.getId());
        return SuccessResponse.success(result);
    }

    @Parameters({
            @Parameter(in = ParameterIn.HEADER, name = "Authorization", required = true),
    })
    @DeleteMapping("/my")
    public SuccessResponse<?> deleteUser(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        userService.deleteUserById(customUserDetails.getId());
        return SuccessResponse.success(204, "DELETED");
    }



    @PostMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Parameters({
            @Parameter(in = ParameterIn.HEADER, name = "Authorization", required = true),
            @Parameter(name = "profileImage", description = "multipart/form-data 형식으로 이미지를 받습니다.")
    })
    public SuccessResponse updateProfileImage(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestPart(value = "profileImage", required = true) MultipartFile profileImage
    ) throws IOException {
        String imageUrl = s3Service.uploadFile(profileImage);
        UserDTO.updateUser result = userService.updateUserProfile(customUserDetails.getId(), imageUrl);
        return SuccessResponse.success(result);
    }

    @PatchMapping(value="/my/password")
    public SuccessResponse updatePassword(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody UserDTO.updateUserPassword request
    ) {
        userService.updateUserPassword(customUserDetails.getId(), request.getPassword());
        return SuccessResponse.success(204, "PASSWORD UPDATED");
    }

}
