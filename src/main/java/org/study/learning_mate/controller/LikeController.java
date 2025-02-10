package org.study.learning_mate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.study.learning_mate.SuccessResponse;
import org.study.learning_mate.downvote.DownVote;
import org.study.learning_mate.downvote.DownVoteService;
import org.study.learning_mate.dto.CustomUserDetails;
import org.study.learning_mate.like.LikeService;
import org.study.learning_mate.post.Post;
import org.study.learning_mate.post.PostService;
import org.study.learning_mate.service.UserService;
import org.study.learning_mate.upvote.UpVote;
import org.study.learning_mate.upvote.UpVoteService;
import org.study.learning_mate.user.User;

@Tag(name = "Like API", description = "Like API")
@RestController
public class LikeController {

    // TODO : upvote, downvote를 이렇게 불러올 것인지?

    private final LikeService likeService;
    private final PostService postService;
    private final UserService userService;
    private final UpVoteService upVoteService;
    private final DownVoteService downVoteService;

    public LikeController(
            LikeService likeService,
            PostService postService,
            UserService userService,
            UpVoteService upVoteService,
            DownVoteService downVoteService
        ) {
        this.likeService = likeService;
        this.postService = postService;
        this.userService = userService;
        this.upVoteService = upVoteService;
        this.downVoteService = downVoteService;
    }

    @Operation(summary = "게시글 like 조회", description = "Lecture, 날강도 글에 좋아요를 눌렀는 지 조회합니다.")
    @Parameters({
            @Parameter(name = "postId", description = "게시글 식별값", required = true),
    })
    @GetMapping("/posts/{postId}/exists")
    public SuccessResponse<?> isExistsLikePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
        ) {
        Boolean isExist = likeService.checkLikeInPost(postId, userDetails.getId());
        return SuccessResponse.success(new String[]{"isExist :" + isExist});
    }

    @Operation(summary = "추천 글 like 조회", description = "추천글에 좋아요를 눌렀는 지 조회합니다.")
    @Parameters({
            @Parameter(name = "upVoteId", description = "추천글 식별값", required = true),
    })
    @GetMapping("/up-votes/{upVoteId}/exists")
    public SuccessResponse<?> isExistsLikeUpVote(
            @PathVariable Long upVoteId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Boolean isExist = likeService.checkLikeInUpVote(upVoteId, userDetails.getId());
        return SuccessResponse.success(new String[]{"isExist :" + isExist});
    }

    @Operation(summary = "비추천 글 like 조회", description = "비추천글에 좋아요를 눌렀는 지 조회합니다.")
    @Parameters({
            @Parameter(name = "downVoteId", description = "비추천글 식별값", required = true),
    })
    @GetMapping("/down-votes/{downVoteId}/exists")
    public SuccessResponse<?> isExistsLikeDownVote(
            @PathVariable Long downVoteId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Boolean isExist = likeService.checkLikeInDownVote(downVoteId, userDetails.getId());
        return SuccessResponse.success(new String[]{"isExist :" + isExist});
    }

    // 좋아요
    @Operation(summary = "게시글 좋아요", description = "게시글(Lecture, 날강도)에 좋아요를 누릅니다.")
    @Parameters({
            @Parameter(in = ParameterIn.HEADER, name = "Authorization", required = true),
            @Parameter(name = "postId", description = "게시글 식별값", required = true),
    })
    @PostMapping("/posts/{postId}/like")
    public SuccessResponse<?> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Post post = postService.findById(postId);
        User user = userService.findUserById(userDetails.getId());
        likeService.createLikeInPost(post, user);
        return SuccessResponse.success(201);
    }

    @Operation(summary = "추천 좋아요", description = "추천글에 좋아요를 누릅니다.")
    @Parameters({
            @Parameter(in = ParameterIn.HEADER, name = "Authorization", required = true),
            @Parameter(name = "upVoteId", description = "추천글 식별값", required = true),
    })
    @PostMapping("/up-vote/{upVoteId}/like")
    public SuccessResponse<?> likeUpVote(
            @PathVariable Long upvoteId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UpVote upVote = upVoteService.getUpVoteById(upvoteId);
        User user = userService.findUserById(userDetails.getId());
        likeService.createLikeInUpVote(upVote, user);
        return SuccessResponse.success(201);
    }

    @Operation(summary = "비추천 좋아요", description = "비추천글에 좋아요를 누릅니다.")
    @Parameters({
            @Parameter(in = ParameterIn.HEADER, name = "Authorization", required = true),
            @Parameter(name = "downVoteId", description = "비추천글 식별값", required = true),
    })
    @PostMapping("/down-vote/{downVoteId}/like")
    public SuccessResponse<?> likeDownVote(
            @PathVariable Long downVoteId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // downvote를 여기서 가져와야 할 지
        DownVote downVote = downVoteService.getDownVoteById(downVoteId);
        User user = userService.findUserById(userDetails.getId());
        likeService.createLikeInDownVote(downVote, user);
        return SuccessResponse.success(201);
    }

    // 좋아요 취소
    @Operation(summary = "게시글 좋아요 취소", description = "게시글의 좋아요를 삭제합니다.")
    @Parameters({
            @Parameter(in = ParameterIn.HEADER, name = "Authorization", required = true),
            @Parameter(name = "postId", description = "게시글 식별값", required = true),
    })
    @DeleteMapping("/posts/{postId}/unlike")
    public SuccessResponse<?> unlikePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        likeService.deleteLikeInPost(userDetails.getId(), postId);
        return SuccessResponse.success(204);
    }

    @Operation(summary = "추천글 좋아요 취소", description = "추천글의 좋아요를 삭제합니다.")
    @Parameters({
            @Parameter(in = ParameterIn.HEADER, name = "Authorization", required = true),
            @Parameter(name = "upVoteId", description = "추천글 식별값", required = true),
    })
    @DeleteMapping("/up-votes/{upVoteId}/unlike")
    public SuccessResponse<?> unlikeUpVote(
            @PathVariable Long upVoteId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        likeService.deleteLikeInUpVote(userDetails.getId(), upVoteId);
        return SuccessResponse.success(204);
    }

    @Operation(summary = "비추천글 좋아요 취소", description = "비추천글의 좋아요를 삭제합니다.")
    @Parameters({
            @Parameter(in = ParameterIn.HEADER, name = "Authorization", required = true),
            @Parameter(name = "downVoteId", description = "비추천글 식별값", required = true),
    })
    @DeleteMapping("/down-votes/{downVoteId}/unlike")
    public SuccessResponse<?> unlikeDownVote(
            @PathVariable Long downVoteId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        likeService.deleteLikeInDownVote(userDetails.getId(), downVoteId);
        return SuccessResponse.success(204);
    }
}
