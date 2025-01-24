package org.study.learning_mate.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.study.learning_mate.SuccessResponse;
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
    @Parameter(name = "userId", description = "유저 식별값")
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
}
