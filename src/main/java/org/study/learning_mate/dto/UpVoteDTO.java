package org.study.learning_mate.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.study.learning_mate.user.User;

import java.util.Date;

public class UpVoteDTO {

    @Getter
    @Setter
    @Builder
    public static class UpVoteResponse {
        private Long id;
        private UserDTO.UserProfile user;
        private String title;
        private String reason;
        private Long likeCount;
        private Date createTime;
        private Date updateTime;
    }

    @Getter
    public static class UpVoteRequest {
        private String title;
        private String reason;
    }
}
