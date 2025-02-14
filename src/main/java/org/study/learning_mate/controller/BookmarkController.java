package org.study.learning_mate.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.study.learning_mate.SuccessResponse;
import org.study.learning_mate.bookmark.BookmarkService;
import org.study.learning_mate.dto.BookmarkDTO;
import org.study.learning_mate.dto.CustomUserDetails;
import org.study.learning_mate.dto.LectureDTO;
import org.study.learning_mate.lecture.LectureService;

import java.util.ArrayList;
import java.util.List;

@RestController
public class BookmarkController {

    private BookmarkService bookmarkService;
    private LectureService lectureService;

    public BookmarkController(
            BookmarkService bookmarkService,
            LectureService lectureService
    ) {
        this.bookmarkService = bookmarkService;
        this.lectureService = lectureService;
    }


    @Parameters({
            @Parameter(in = ParameterIn.HEADER, name = "Authorization", required = true),
            @Parameter(name = "platform", required = false, description = "플랫폼 이름"),
    })
    @GetMapping("/bookmarks")
    public SuccessResponse<List<LectureDTO.LectureResponse>> getBookmarkLectures(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String platform,
            Pageable pageable
    ) {
        Long userId = userDetails.getId();
        List<Long> bookmarks = new ArrayList<>();
        if (platform == null) {
            bookmarks = bookmarkService.getBookmarks(
                userId,
                pageable
            );
        } else {
            bookmarks = bookmarkService.getBookmarks(
                    userId,
                    platform,
                    pageable
            );
        }


        List<LectureDTO.LectureResponse> result = lectureService.findByLectureIds(bookmarks);
        return SuccessResponse.success(result);
    }

    @PostMapping("/bookmarks")
    public SuccessResponse<?> addBookmarkLecture(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Bookmark 생성 객체",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookmarkDTO.createBookmarkRequest.class)
                    )
            )
            @RequestBody BookmarkDTO.createBookmarkRequest request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        Long userId = customUserDetails.getId();
        bookmarkService.addBookmark(request.getPostId(), userId);
        return SuccessResponse.success(201, "CREATED");
    }
}
