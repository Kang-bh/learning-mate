package org.study.learning_mate.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class DemandLectureDTO {

    @Getter
    @Setter
    @Builder
    public static class DemandLectureResponse {
        private Long id;
        private String title;
        private String content;
        private Long likes;
        private Long comments;
        private Long views;
        private Date createTime;
        private Date updateTime;
    }

    @Getter
    @Setter
    @Builder
    public static class DemandLectureDetailResponse {
        private Long id;
        private String title;
        private String content;
        private Long likes;
        private Long comments;
        private Long views;
        private Date createTime;
        private Date updateTime;
        private UserDTO.UserProfile user;
    }

    @Setter
    @Getter
    public static class createDemandLectureRequest {
        private String title;
        private String content;
    }

    @Setter
    @Getter
    public static class updateDemandLectureRequest {
        private String title;
        private String content;
    }
}
