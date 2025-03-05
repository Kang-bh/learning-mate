package org.study.learning_mate.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
            @Parameter(name = "sort", description = "정렬 순서", required = false, example = "id,asc"),
            @Parameter(name = "page", description = "페이지 수", required = false, example = "0"),
            @Parameter(name = "size", description = "크기", required = false, example = "10"),
    })
    @GetMapping("/bookmarks")
    public SuccessResponse<List<LectureDTO.LectureResponse>> getBookmarkLectures(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String platform,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
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

    @Parameters({
            @Parameter(in = ParameterIn.HEADER, name = "Authorization", required = true),
    })
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

    @Parameters({
            @Parameter(in = ParameterIn.HEADER, name = "Authorization", required = true),
            @Parameter(name = "postId", required = true, description = "게시물 식별값"),
    })
    @DeleteMapping("/bookmarks")
    public SuccessResponse<?> deleteBookmarkLecture(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam Long postId
    ) {
        bookmarkService.deleteBookmark(postId, customUserDetails.getId());
        return SuccessResponse.success(204, "DELETED");
    }

    @Parameters({
            @Parameter(in = ParameterIn.HEADER, name = "Authorization", required = true),
            @Parameter(name = "postId", required = true, description = "게시물 식별값"),
    })
    @GetMapping("/bookmarks/exist")
    public SuccessResponse<?> isBookmarkExist(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam Long postId
        ) {
        boolean result = bookmarkService.isExistBookmark(postId, customUserDetails.getId());
        return SuccessResponse.success(result);
    }
}
