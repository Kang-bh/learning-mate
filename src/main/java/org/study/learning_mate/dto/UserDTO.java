package org.study.learning_mate.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


public class UserDTO {

    // todo : Info update User 통합
    @Getter
    @AllArgsConstructor
    @Builder
    public static class Info {
        private Long id;
        private String role;
        private String email;
        private String name;
        private String profileImage;
    }


    @Getter
    @Setter
    @Builder
    public static class updateUser {
        private String name;
        private String profileImage;
        private String backGroundImage;
        private String email;
    }

    @Getter
    @Setter
    @Builder
    public static class UserProfile {
        private Long id;
        private String name;
        private String profileImage;
    }

    @Getter
    @Setter
//    @Builder
    public static class Join {
        private String name;
        private final String email;
        private String password;

        @JsonCreator
        public Join(
                @JsonProperty("name") String name,
                @JsonProperty("email") String email,
                @JsonProperty("password") String password
        ) {
            this.name = name;
            this.password = password;
            this.email = email;
        }
    }

    @Getter
    @Setter
    public static class Login {
        private String userId;
        private String password;
    }

    @Getter
    @Setter
    public static class updateUserPassword {
        private String password;
    }
}
