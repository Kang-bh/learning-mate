package org.study.learning_mate.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.study.learning_mate.comment.Comment;
import org.study.learning_mate.dto.CommentDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper {

    private final UserMapper userMapper;

    public CommentMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public CommentDTO.CommentResponse toCommentDTO(Comment comment) {
        return CommentDTO.CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .user(userMapper.toUserProfile(comment.getUser()))
                .createTime(comment.getCreatedAt())
                .updateTime(comment.getUpdatedAt())
                .build();
    }

    public List<CommentDTO.CommentResponse> toCommentListDTO(List<Comment> comments) {
        List<CommentDTO.CommentResponse> commentList = new ArrayList<>();

        for (Comment comment : comments) {
            commentList.add(toCommentDTO(comment));
        }

        return commentList;
    }

    public Page<CommentDTO.CommentResponse> toCommentPageDTO(Page<Comment> commentPage) {
        List<CommentDTO.CommentResponse> result = commentPage.getContent().stream()
                .map(this::toCommentDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(result, commentPage.getPageable(), commentPage.getTotalElements());

    }
}
