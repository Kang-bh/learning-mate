package org.study.learning_mate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.study.learning_mate.SuccessResponse;
import org.study.learning_mate.demandlecture.DemandLectureService;
import org.study.learning_mate.dto.CommentDTO;
import org.study.learning_mate.dto.CustomUserDetails;
import org.study.learning_mate.dto.DemandLectureDTO;
import org.study.learning_mate.service.UserService;
import org.study.learning_mate.user.User;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Tag(name = "날.강.도. API", description = "날.강.도. API")
@RestController
public class DemandLectureController {

    private final DemandLectureService demandLectureService;
    private final UserService userService;

    public DemandLectureController(DemandLectureService demandLectureService, UserService userService) {
        this.demandLectureService = demandLectureService;
        this.userService = userService;
    }

    @Operation(summary = "날.강.도. 게시글 조회", description = "날.강.도. 게시글을 조회합니다.")
    @Parameters({
            @Parameter(name = "sort", description = "정렬 순서", required = false, example = "demandLecturePK.post.id,asc"),
            @Parameter(name = "page", description = "페이지 수", required = false, example = "0"),
            @Parameter(name = "size", description = "크기", required = false, example = "10"),
    })
    @GetMapping("/demand-lectures")
    public SuccessResponse<List<DemandLectureDTO.DemandLectureResponse>> getDemandLectures(@PageableDefault(size = 10, sort = "demandLecturePK.post.id", direction = Sort.Direction.DESC)  Pageable pageable) {
        List<DemandLectureDTO.DemandLectureResponse> result = demandLectureService.findAllDemandLectureList(pageable);
        return SuccessResponse.success(result);
    }

    @Operation(summary = "날.강.도. 게시글 조회", description = "날.강.도. 게시글을 조회합니다.")
    @Parameters({
            @Parameter(name = "demandLectureId", description = "날.강.도 게시글 식별자", required = true),
    })
    @GetMapping("/demand-lectures/{demandLectureId}")
    public SuccessResponse<DemandLectureDTO.DemandLectureDetailResponse> getDemandLectureDetail(@PathVariable(value="demandLectureId") Long demandLectureId) {
        DemandLectureDTO.DemandLectureDetailResponse result = demandLectureService.findDemandLectureById(demandLectureId);
        return SuccessResponse.success(result);
    }

    @Operation(summary = "날.강.도. 게시글 생성", description = "날.강.도. 게시글을 생성합니다.", security = @SecurityRequirement(name = "bearer-key"))
    @PostMapping("/demand-lectures")
    @Parameters({
            @Parameter(in = ParameterIn.HEADER, name = "Authorization", required = true),
    })
    public SuccessResponse<?> createDemandLecture(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "날.강.도. 생성 객체",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DemandLectureDTO.createDemandLectureRequest.class)
                    )
            )
            @RequestBody(required = true) DemandLectureDTO.createDemandLectureRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
        ) {
        Long userId = userDetails.getId();
        User user = userService.findUserById(userId);
        demandLectureService.createDemandLecture(request, user);

        return SuccessResponse.success("SUCCESS");
    }

    @Parameters({
            @Parameter(in = ParameterIn.HEADER, name = "Authorization", required = true),
            @Parameter(name = "demandLectureId", description = "날.강.도. 게시글 식별자", required = true),
    })
    @PutMapping("/demand-lectures/{demandLectureId}")
    public SuccessResponse<DemandLectureDTO.DemandLectureDetailResponse> updateDemandLecture(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "날.강.도. 수정 객체",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DemandLectureDTO.updateDemandLectureRequest.class)
                    )
            )
            @RequestBody(required = true) DemandLectureDTO.updateDemandLectureRequest request,
            @PathVariable Long demandLectureId,
            @AuthenticationPrincipal CustomUserDetails userDetails
     ) {
        Long userId = userDetails.getId();
        User user = userService.findUserById(userId);
        DemandLectureDTO.DemandLectureDetailResponse result = demandLectureService.updateDemandLecture(request, user, demandLectureId);
        return SuccessResponse.success(result);
    }

    @Parameters({
            @Parameter(in = ParameterIn.HEADER, name = "Authorization", required = true),
            @Parameter(name = "demandLectureId", description = "날.강.도. 게시글 식별자", required = true),
    })
    @DeleteMapping("/demand-lectures/{demandLectureId}")
    public SuccessResponse<?> deleteDemandLecture(
            @PathVariable Long demandLectureId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) throws AccessDeniedException {
        Long userId = userDetails.getId();
        User user = userService.findUserById(userId);
        demandLectureService.deleteDemandLectureById(demandLectureId, user);
        return SuccessResponse.success(204, "DELETE SUCCESS");
    }

}
