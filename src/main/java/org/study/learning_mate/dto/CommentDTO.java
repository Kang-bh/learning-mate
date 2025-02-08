package org.study.learning_mate.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class CommentDTO {

    @Getter
    @Setter
    @Builder
    public static class CommentResponse {
        private Long id;
        private String content;
        private UserDTO.UserProfile user;
        private Date createTime;
        private Date updateTime;
    }

//    @Getter
//    @Setter
//    @Builder
//    public static class CreateCommentRequest {
//        private Long postId;
//        private String content;
//    }
}
