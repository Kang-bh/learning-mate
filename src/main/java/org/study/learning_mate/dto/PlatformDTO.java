package org.study.learning_mate.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

public class PlatformDTO {

    @Getter
    @Setter
    @Builder
    public static class PlatformResponse {
        private Long id;
        private String title;
        private String logoUrl;
        private String url;
    }
}
