package org.study.learning_mate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.study.learning_mate.SuccessResponse;
import org.study.learning_mate.UserRepository;
import org.study.learning_mate.dto.CustomUserDetails;
import org.study.learning_mate.dto.DownVoteDTO;
import org.study.learning_mate.dto.UpVoteDTO;
import org.study.learning_mate.service.UserService;
import org.study.learning_mate.upvote.UpVote;
import org.study.learning_mate.upvote.UpVoteService;
import org.study.learning_mate.user.User;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Tag(name = "UpVote API", description = "UpVote API")
@RestController
public class UpVoteController {

    private final UpVoteService upVoteService;
    private final UserService userService;
    private final UserRepository userRepository;

    public UpVoteController(
        UpVoteService upVoteService,
        UserService userService,
        UserRepository userRepository) {
        this.upVoteService = upVoteService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Operation(summary = "날강도 게시글 추천 조회", description = "Lecture, 날강도 글의 추천글을 조회합니다..")
    @GetMapping("/posts/{postId}/up-votes")
    public SuccessResponse<List<UpVoteDTO.UpVoteResponse>> getUpVotes(
            @PathVariable Long postId,
            Pageable pageable
    ) {
        List<UpVoteDTO.UpVoteResponse> result = upVoteService.getUpVotes(postId, pageable);
        return SuccessResponse.success(result);
    }

    @Operation(summary = "날강도 게시글 추천글 생성", description = "Lecture, 날강도 글의 추천글을 작성합니다.")
    @Parameters({
            @Parameter(name = "postId", description = "게시글 식별자", required = true),
    })
    @PostMapping("/posts/{postId}/up-votes")
    public SuccessResponse<?> createUpVote(
            @PathVariable Long postId,
            @RequestBody UpVoteDTO.UpVoteRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userService.findUserById(userDetails.getId());
        upVoteService.createUpVote(postId, request, user);
        return SuccessResponse.success(201, "CREATED");
    }

    @Operation(summary = "날강도 게시글 추천글 수정", description = "Lecture, 날강도 글의 추천글을 수정합니다.")
    @Parameters({
            @Parameter(name = "upVoteId", description = "추천글 식별자", required = true),
    })
    @PutMapping("/up-votes/{upVoteId}")
    public SuccessResponse<UpVoteDTO.UpVoteResponse> updateUpVote(
            @PathVariable Long upVoteId,
            @RequestBody UpVoteDTO.UpVoteRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) throws AccessDeniedException {
        UpVoteDTO.UpVoteResponse result = upVoteService.updateUpVote(upVoteId, request, userDetails.getId());
        return SuccessResponse.success(result);
    }

    @Operation(summary = "날강도 게시글 삭제", description = "Lecture, 날강도 글의 추천글을 삭제합니다.")
    @Parameters({
            @Parameter(name = "upVoteId", description = "추천글 식별자", required = true),
    })
    @DeleteMapping("/up-votes/{upVoteId}")
    public SuccessResponse<?> deleteUpVote(
            @PathVariable Long upVoteId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) throws AccessDeniedException {
        upVoteService.deleteUpVote(upVoteId, userDetails.getId());
        return SuccessResponse.success(204);
    }
}
