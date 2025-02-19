package org.study.learning_mate.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.study.learning_mate.SuccessResponse;
import org.study.learning_mate.dto.UserDTO;
import org.study.learning_mate.global.ErrorResponse;
import org.study.learning_mate.service.JoinService;

@Tag(name = "Join API", description = "Join 관련 API")
@RestController
public class JoinController {

    private final JoinService joinService;

    public JoinController(JoinService joinService) {
        this.joinService = joinService;
    }

    @PostMapping("/join")
    @Parameter(name = "userId", description = "userId")
    @Parameter(name = "password", description = "password")
    public SuccessResponse<UserDTO.Info> join(
            @RequestBody UserDTO.Join joinDTO
    )  throws Exception, ErrorResponse {

        System.out.println(11);
        UserDTO.Info result = joinService.join(joinDTO);
        System.out.println(22);

        return SuccessResponse.success(result);
    }
}
