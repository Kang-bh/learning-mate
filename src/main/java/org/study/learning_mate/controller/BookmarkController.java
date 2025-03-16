package org.study.learning_mate.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.study.learning_mate.SuccessResponse;
import org.study.learning_mate.bookmark.Bookmark;
import org.study.learning_mate.bookmark.BookmarkService;
import org.study.learning_mate.dto.BookmarkDTO;
import org.study.learning_mate.dto.CustomUserDetails;
import org.study.learning_mate.dto.LectureDTO;
import org.study.learning_mate.lecture.LectureService;
import org.study.learning_mate.platform.PlatformTypeManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class BookmarkController {

    private BookmarkService bookmarkService;
    private final PlatformTypeManager platformTypeManager;
    private LectureService lectureService;

    public BookmarkController(
            BookmarkService bookmarkService,
            LectureService lectureService,
            PlatformTypeManager platformTypeManager
    ) {
        this.platformTypeManager = platformTypeManager;
        this.bookmarkService = bookmarkService;
        this.lectureService = lectureService;
    }

    @Parameters({
            @Parameter(in = ParameterIn.HEADER, name = "Authorization", required = true),
            @Parameter(description = "강의 플랫폼 (쉼표로 구분된 문자열, 예: youtube,udemy)"),
            @Parameter(name = "sort", description = "정렬 순서", required = false, example = "id,asc"),
            @Parameter(name = "page", description = "페이지 수", required = false, example = "0"),
            @Parameter(name = "size", description = "크기", required = false, example = "10"),
    })
    @GetMapping("/bookmarks")
    public SuccessResponse<Page<LectureDTO.LectureResponse>> getBookmarkLectures(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) @Parameter(description = "강의 플랫폼 (쉼표로 구분된 문자열, 예: youtube,udemy)") String platforms,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Long userId = userDetails.getId();
        Page<Long> bookmarks = new PageImpl<>(Collections.EMPTY_LIST);
        List<PlatformTypeManager.PlatformType> platformType = new ArrayList<>();

        if (platforms == null) {
            bookmarks = bookmarkService.getBookmarks(
                userId,
                pageable
            );
        } else {
            platformType = platformTypeManager.getPlatformTypesByNames(platforms);
            bookmarks = bookmarkService.getBookmarks(
                    userId,
                    platformType,
                    pageable
            );
        }

        // todo : page를 Service 안에서 생성되도록
        System.out.println("bookmarks: " + bookmarks);
        List<LectureDTO.LectureResponse> result = lectureService.findByLectureIds(bookmarks.stream().toList());
        Page<LectureDTO.LectureResponse> pagingResult = new PageImpl<>(result, bookmarks.getPageable(), bookmarks.getTotalElements());
        return SuccessResponse.success(pagingResult);
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
