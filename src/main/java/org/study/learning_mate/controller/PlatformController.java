package org.study.learning_mate.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.study.learning_mate.SuccessResponse;
import org.study.learning_mate.dto.CustomUserDetails;
import org.study.learning_mate.dto.PlatformDTO;
import org.study.learning_mate.platform.PlatformService;

import java.util.List;

@Tag(name = "Platform API", description = "API related with Platform CRUD")
@RestController
public class PlatformController {

    private final PlatformService platformService;

    public PlatformController(PlatformService platformService) {
        this.platformService = platformService;
    }

    @Operation(summary = "인강 플랫폼 목록 조회", description = "인강 플랫폼 종류들을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class
                            ))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/platforms")
    public SuccessResponse<?> getPlatforms() {
        List<PlatformDTO.PlatformResponse> result = platformService.getPlatforms();

        return SuccessResponse.success(result);
    }
}
