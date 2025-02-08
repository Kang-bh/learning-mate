package org.study.learning_mate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Getter
    @Setter
    @Builder
    @Schema(description = "Comment Request DTO")
    public static class CommentRequest {
        @Schema(description = "내용")
        private String content;
    }
}
