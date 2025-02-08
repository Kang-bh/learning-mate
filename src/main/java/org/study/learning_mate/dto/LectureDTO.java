package org.study.learning_mate.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class LectureDTO {

    @Getter
    @Setter
    @Builder
    public static class LectureResponse {
        private Long id;
        private String title;
        private String url;
        private Long likes;
        private Long dislikes;
        private Long views;
        private Long comments;
        private PlatformDTO.PlatformResponse platform;
    }

    @Getter
    public static class createLectureRequest {
        private String url;
    }
}
