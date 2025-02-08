package org.study.learning_mate.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.study.learning_mate.user.User;

import java.util.Date;

public class DownVoteDTO {


    @Getter
    @Setter
    @Builder
    public static class DownVoteResponse {
        private Long id;
        private UserDTO.UserProfile user;
        private String title;
        private String reason;
        private Long likeCount;
        private Date createAt;
        private Date updateAt;
    }

    @Getter
    public static class DownVoteRequest {
        private String title;
        private String reason;
    }
}
