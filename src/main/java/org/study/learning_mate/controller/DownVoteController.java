package org.study.learning_mate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.study.learning_mate.SuccessResponse;
import org.study.learning_mate.downvote.DownVoteService;
import org.study.learning_mate.dto.CustomUserDetails;
import org.study.learning_mate.dto.DownVoteDTO;
import org.study.learning_mate.service.UserService;
import org.study.learning_mate.user.User;
import org.study.learning_mate.utils.DownVoteMapper;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Tag(name = "DownVote API", description = "DownVote API")
@RestController
public class DownVoteController {

    private final DownVoteService downVoteService;
    private final UserService userService;

    public DownVoteController(
            DownVoteService downVoteService,
            UserService userService
    ) {
        this.downVoteService = downVoteService;
        this.userService = userService;
    }

    @Operation(summary = "날강도 게시글 비추천 조회", description = "Lecture, 날강도 글의 비추천글을 조회합니다..")
    @Parameters({
            @Parameter(name = "postId", description = "게시글(Lecture, 날.강.도) 식별자", required = true),
    })
    @GetMapping("/posts/{postId}/down-votes")
    public SuccessResponse<List<DownVoteDTO.DownVoteResponse>> getDownVotes(@PathVariable Long postId, Pageable pageable) {
        List<DownVoteDTO.DownVoteResponse> result = downVoteService.getDownVotes(postId, pageable);
        return SuccessResponse.success(result);
    }

    @Operation(summary = "날강도 게시글 비추천글 생성", description = "Lecture, 날강도 글의 비추천글을 작성합니다.")
    @Parameters({
            @Parameter(name = "postId", description = "게시글(Lecture, 날.강.도) 식별자", required = true),
    })
    @PostMapping("/posts/{postId}/down-votes")
    public SuccessResponse<?> createDownVote(
            @PathVariable Long postId,
            @RequestBody DownVoteDTO.DownVoteRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userService.findUserById(userDetails.getId());
        downVoteService.createDownVote(postId, request, user);
        return SuccessResponse.success(201, "CREATED");
    }

    @Operation(summary = "날강도 게시글 비추천글 수정", description = "Lecture, 날강도 글의 비추천글을 수정합니다.")
    @Parameters({
            @Parameter(name = "downVoteId", description = "비추천 게시글 식별자", required = true),
    })
    @PutMapping("/down-votes/{downVoteId}")
    public SuccessResponse<DownVoteDTO.DownVoteResponse> updateDownVote(@PathVariable Long downVoteId, @RequestBody DownVoteDTO.DownVoteRequest request) {
        DownVoteDTO.DownVoteResponse result = downVoteService.updateDownVote(downVoteId, request);
        return SuccessResponse.success(result);
    }

    @Operation(summary = "날강도 게시글 비추천글 삭제", description = "Lecture, 날강도 글의 비추천글을 삭제합니다.")
    @Parameters({
            @Parameter(name = "downVoteId", description = "비추천 게시글 식별자", required = true),
    })
    @DeleteMapping("/down-votes/{downVoteId}")
    public SuccessResponse<?> deleteDownVote(@PathVariable Long downVoteId, @AuthenticationPrincipal CustomUserDetails customUserDetails) throws AccessDeniedException {
        downVoteService.deleteDownVote(downVoteId, customUserDetails.getId());
        return SuccessResponse.success(204, "DELETED");
    }
}
