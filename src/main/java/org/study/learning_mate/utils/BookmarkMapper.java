package org.study.learning_mate.utils;

import org.springframework.stereotype.Component;
import org.study.learning_mate.bookmark.Bookmark;
import org.study.learning_mate.dto.BookmarkDTO;

import java.util.ArrayList;
import java.util.List;

@Component
public class BookmarkMapper {

    public BookmarkDTO.BookmarkResponse toBookmarkDTO(Bookmark bookmark) {
        return BookmarkDTO.BookmarkResponse.builder()
                .id(bookmark.getId())
                .build();
    }

    public List<BookmarkDTO.BookmarkResponse> toBookmarkListDTO(List<Bookmark> bookmarks) {
        List<BookmarkDTO.BookmarkResponse> bookmarkResponses = new ArrayList<>();
        for (Bookmark bookmark : bookmarks) {
            bookmarkResponses.add(toBookmarkDTO(bookmark));
        }
        return bookmarkResponses;
    }
}
