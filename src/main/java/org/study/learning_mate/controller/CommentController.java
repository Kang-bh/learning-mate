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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.study.learning_mate.SuccessResponse;
import org.study.learning_mate.comment.CommentService;
import org.study.learning_mate.dto.CommentDTO;
import org.study.learning_mate.dto.CustomUserDetails;
import org.study.learning_mate.global.PagedSuccessResponse;
import org.study.learning_mate.service.UserService;
import org.study.learning_mate.user.User;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Tag(name = "Comment API", description = "Comment API")
@RestController
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    public CommentController(
            CommentService commentService,
            UserService userService
    ) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @Operation(summary = "게시글에 존재하는 댓글 조회", description = "Lecture, 날강도 글에 존재하는 댓글들을 조회합니다.")
    @Parameters({
            @Parameter(name = "postId", description = "게시글 식별자", required = true),
    })
    @GetMapping("/posts/{postId}/comments")
    public PagedSuccessResponse<CommentDTO.CommentResponse> findAllByPostId(@PathVariable Long postId, Pageable pageable) {
        Page<CommentDTO.CommentResponse> result = commentService.findAllCommentByPostId(postId, pageable);
        return PagedSuccessResponse.success(result);
    }

    @Operation(summary = "게시글에 댓글 생성", description = "Lecture, 날강도 글에 댓글들을 생성합니다.")
    @Parameters({
            @Parameter(in = ParameterIn.HEADER, name = "Authorization", required = true),
            @Parameter(name = "postId", description = "게시글 식별자", required = true),
    })
    @PostMapping("/posts/{postId}/comments")
    public SuccessResponse<CommentDTO.CommentResponse> saveComment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Comment 생성 객체",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommentDTO.CommentRequest.class)
                    )
            )
            @RequestBody CommentDTO.CommentRequest content,
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userService.findUserById(userDetails.getId());
        CommentDTO.CommentResponse result = commentService.createComment(content, postId, user);
        return SuccessResponse.success(result);
    }

    @Operation(summary = "게시글에 댓글 수정", description = "Lecture, 날강도 글에 작성한 댓글들을 수정합니다..")
    @Parameters({
            @Parameter(in = ParameterIn.HEADER, name = "Authorization", required = true),
            @Parameter(name = "commentId", description = "댓글 식별자", required = true),
    })
    @PutMapping("/comments/{commentId}")
    public SuccessResponse<CommentDTO.CommentResponse> updateComment(
            @Parameter(description = "댓글 식별자") @PathVariable Long commentId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Comment 수정 객체",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommentDTO.CommentRequest.class)
                    )
            )
            @RequestBody CommentDTO.CommentRequest request) {
        CommentDTO.CommentResponse result = commentService.updateComment(request.getContent(), commentId);
        return SuccessResponse.success(result);
    }

    @Operation(summary = "게시글에 댓글 삭제", description = "Lecture, 날강도 글에 작성한 댓글들을 삭제합니다..")
    @Parameters({
            @Parameter(in = ParameterIn.HEADER, name = "Authorization", required = true),
            @Parameter(name = "commentId", description = "댓글 식별자", required = true),
    })
    @DeleteMapping("/comments/{commentId}")
    public SuccessResponse<?> deleteComment(@Parameter(description = "댓글 식별자") @PathVariable Long commentId, @AuthenticationPrincipal CustomUserDetails userDetails) throws AccessDeniedException {
        User user = userService.findUserById(userDetails.getId());
        commentService.deleteComment(commentId, user);

        return SuccessResponse.success(204, "DELETE SUCCESS");
    }
}
