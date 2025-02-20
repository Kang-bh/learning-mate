package org.study.learning_mate.bookmark;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.study.learning_mate.UserRepository;
import org.study.learning_mate.dto.BookmarkDTO;
import org.study.learning_mate.post.Post;
import org.study.learning_mate.post.PostRepository;
import org.study.learning_mate.user.User;
import org.study.learning_mate.utils.BookmarkMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class BookmarkService {
    private BookmarkRepository bookmarkRepository;
    private BookmarkMapper bookmarkMapper;
    private UserRepository userRepository;
    private PostRepository postRepository;

    public BookmarkService(
            BookmarkRepository bookmarkRepository,
            BookmarkMapper bookmarkMapper,
            UserRepository userRepository,
            PostRepository postRepository
            ) {
        this.bookmarkRepository = bookmarkRepository;
        this.bookmarkMapper = bookmarkMapper;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public List<Long> getBookmarks(
                Long userId,
                String platform,
                Pageable pageable
            ) {

        Page<Bookmark> result = bookmarkRepository.findBookmarksByPlatformTitleAndUserId(platform, userId, pageable);
        return extractIds(result.stream().toList());
    }

    public List<Long> getBookmarks(
            Long userId,
            Pageable pageable
    ) {
        Page<Bookmark> result = bookmarkRepository.findByUser_Id(userId, pageable);
        return extractIds(result.stream().toList());
    }

    public void addBookmark(Long postId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);
        log.info("111");
        Post post = postRepository.findById(postId).orElseThrow(NoSuchElementException::new);

        log.info("222");
        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .post(post)
                .build();

        bookmarkRepository.save(bookmark);
        return;
    }

    private List<Long> extractIds(List<Bookmark> bookmarks) {
        List<Long> ids = new ArrayList<>();
        for (Bookmark entity : bookmarks) {
            ids.add(entity.getId());
        }

        return ids;
    }
}
