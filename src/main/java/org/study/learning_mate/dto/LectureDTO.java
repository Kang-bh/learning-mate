package org.study.learning_mate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

public class LectureDTO {

    @Getter
    @Setter
    @Builder
    public static class LectureResponse {
        private Long id;
        private String title;
        private String description;
        private String url;
        private Long likes;
        private Long dislikes;
        private Long views;
        private Long comments;
        private PlatformDTO.PlatformResponse platform;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
    }

    @Getter
    @Schema(description = "Lecture Request DTO")
    public static class createLectureRequest {
        @Schema(description = "강의 url")
        private String url;
    }


}
