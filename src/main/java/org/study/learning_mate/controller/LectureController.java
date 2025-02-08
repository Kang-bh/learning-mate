package org.study.learning_mate.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.study.learning_mate.SuccessResponse;
import org.study.learning_mate.dto.LectureDTO;
import org.study.learning_mate.lecture.Lecture;
import org.study.learning_mate.lecture.LectureService;
import org.study.learning_mate.platform.PlatformTypeManager;
import org.study.learning_mate.platform.PlatformTypeManager.PlatformType;
import org.study.learning_mate.service.CrawlService;

import javax.management.InstanceAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;

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
    public SuccessResponse<List<LectureDTO.LectureResponse>> getLectures (
            Pageable pageable,
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) String title
    ) {
        List<LectureDTO.LectureResponse> responses = new ArrayList<>();

        // if platform -> enum
        // overloading
        if (platform == null && title.isEmpty()) { // only pagable;
             responses = lectureService.getLectures(pageable);
        } else if (platform == null && !title.isEmpty()) { // only title with default pagable
            responses = lectureService.getLectures(title, pageable);
        } else if (platform != null && title.isEmpty()) {
            responses = lectureService.getLectures(platformTypeManager.getPlatformTypeByName(platform), pageable);
        } else if (platform != null && !title.isEmpty()) {
            responses = lectureService.getLectures(title, platformTypeManager.getPlatformTypeByName(platform), pageable);
        }


        return SuccessResponse.success(responses);
    }

    @PostMapping("/lectures")
    public SuccessResponse<?> createLecture (
            @RequestBody(required = true) LectureDTO.createLectureRequest request
    ) throws InstanceAlreadyExistsException {
        // TODO : Q
        // check lectureService.createLecture(url);
//        String title = crawlService.crawlLectureTitle(url);
        lectureService.createLecture(request.getUrl());

        return SuccessResponse.success("SUCCESS");
    }
}
