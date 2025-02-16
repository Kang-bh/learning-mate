package org.study.learning_mate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.study.learning_mate.SuccessResponse;
import org.study.learning_mate.dto.LectureDTO;
import org.study.learning_mate.global.PagedSuccessResponse;
import org.study.learning_mate.lecture.LectureService;
import org.study.learning_mate.platform.PlatformTypeManager;
import org.study.learning_mate.platform.PlatformTypeManager.PlatformType;
import org.study.learning_mate.service.CrawlService;

import javax.management.InstanceAlreadyExistsException;

@Tag(name = "Lecture API", description = " API related with Lecture CRUD")
@RestController
public class LectureController {

    private final LectureService lectureService;
    private final PlatformTypeManager platformTypeManager; // TODO : 올바른 DI일지 확인
    private final CrawlService crawlService;

    public LectureController(
            LectureService lectureService,
            PlatformTypeManager platformTypeManager,
            CrawlService crawlService
    ) {
        this.lectureService = lectureService;
        this.platformTypeManager = platformTypeManager;
        this.crawlService =  crawlService;
    }

    @GetMapping("/lectures")
    @Parameters({
            @Parameter(name = "platform", description = "강의 플랫폼", required = true),
            @Parameter(name = "title", description = "강의 제목", required = true)
    })
    @Operation(summary = "강의 목록 조회", description = "플랫폼과 제목으로 강의를 검색합니다.")
    public PagedSuccessResponse<LectureDTO.LectureResponse> getLectures(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) @Parameter(description = "강의 플랫폼") String platform,
            @RequestParam(required = false) @Parameter(description = "강의 제목") String title
    ) {
        Page<LectureDTO.LectureResponse> responses;
        PlatformType platformType = null;

        if (platform != null) {
            platformType = platformTypeManager.getPlatformTypeByName(platform);
        }

        if (platform == null && title == null) {
            responses = lectureService.getLectures(pageable);
        } else if (platform == null) {
            responses = lectureService.getLectures(title, pageable);
        } else if (title == null) {
            responses = lectureService.getLectures(platformType, pageable);
        } else {
            responses = lectureService.getLectures(title, platformType, pageable);
        }

        return PagedSuccessResponse.success(responses);
    }


    @PostMapping("/lectures")
    @Parameters({
            @Parameter(in = ParameterIn.HEADER, name = "Authorization", required = true),
    })
    public SuccessResponse<?> createLecture (
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Lecture 생성 객체",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LectureDTO.createLectureRequest.class)
                    )
            )
            @RequestBody(required = true) LectureDTO.createLectureRequest request
    ) throws InstanceAlreadyExistsException {
        lectureService.createLecture(request.getUrl());

        return SuccessResponse.success("SUCCESS");
    }
}
