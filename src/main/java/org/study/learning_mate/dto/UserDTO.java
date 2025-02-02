package org.study.learning_mate.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


public class UserDTO {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Info {
        private Long id;
        private String role;
        private String email;
        private String name;
    }

    @Getter
    @Setter
//    @Builder
    public static class Join {
        private final String email;
        private String password;

        @JsonCreator
        public Join(
                @JsonProperty("email") String email,
                @JsonProperty("password") String password
        ) {
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
}
