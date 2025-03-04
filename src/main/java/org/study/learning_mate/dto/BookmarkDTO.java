package org.study.learning_mate.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class BookmarkDTO {

    @Getter
    @Setter
    @Builder
    public static class BookmarkResponse {
        private Long id;
    }

    @Getter
    @Setter
    @Builder
    public static class createBookmarkRequest {
        private Long postId;
    }

    public static class isExistBookmarkRequest {
        private Long postId;
    }
}
