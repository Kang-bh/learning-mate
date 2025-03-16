package org.study.learning_mate.bookmark;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.study.learning_mate.UserRepository;
import org.study.learning_mate.dto.BookmarkDTO;
import org.study.learning_mate.lecture.Lecture;
import org.study.learning_mate.lecture.LectureSpecification;
import org.study.learning_mate.platform.PlatformTypeManager;
import org.study.learning_mate.post.Post;
import org.study.learning_mate.post.PostRepository;
import org.study.learning_mate.user.User;
import org.study.learning_mate.utils.BookmarkMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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

    public Page<Long> getBookmarks(
                Long userId,
                List<PlatformTypeManager.PlatformType> platforms,
                Pageable pageable
            ) {


        log.debug("Pageable: {}", pageable);
        log.debug("Sort: {}", pageable.getSort());

        Specification<Bookmark> spec = BookmarkSpecification.filterBy(userId, platforms);
        List<Sort.Order> sortedOrders = pageable.getSort().stream()
                .map(order -> new Sort.Order(order.getDirection(), switchKeyName(order.getProperty())))
                .collect(Collectors.toList());

        Sort newSort = Sort.by(sortedOrders);

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);

        log.debug("Pageable: {}", sortedPageable);
        log.debug("Sort: {}", sortedPageable.getSort());
        Page<Bookmark> results = bookmarkRepository.findAll(spec, sortedPageable);

        return new PageImpl<>(extractIds(results.stream().toList()), results.getPageable(), results.getTotalElements());
    }

    public Page<Long> getBookmarks(
            Long userId,
            Pageable pageable
    ) {
        List<Sort.Order> sortedOrders = pageable.getSort().stream()
                .map(order -> new Sort.Order(order.getDirection(), switchKeyName(order.getProperty())))
                .collect(Collectors.toList());

        Sort newSort = Sort.by(sortedOrders);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);

        Page<Bookmark> results = bookmarkRepository.findByUser_Id(userId, sortedPageable);
        return new PageImpl<>(extractIds(results.stream().toList()), results.getPageable(), results.getTotalElements());
        //        return extractIds(result.stream().toList());
    }

    public void addBookmark(Long postId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);
        log.info("postId" + postId + "userId" + userId);
        Post post = postRepository.findById(postId).orElseThrow(NoSuchElementException::new);

        log.info("222");
        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .post(post)
                .build();

        bookmarkRepository.save(bookmark);
        return;
    }

    @Transactional
    public void deleteBookmark(Long postId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);

        bookmarkRepository.deleteBookmarkByUser_IdAndPost_Id(userId, postId);
        return;
    }

    public boolean isExistBookmark(Long postId, Long userId) {
        boolean result = bookmarkRepository.existsByUser_IdAndPost_Id(userId, postId);
        return result;
    }

    private List<Long> extractIds(List<Bookmark> bookmarks) {
        List<Long> ids = new ArrayList<>();
        for (Bookmark entity : bookmarks) {
            ids.add(entity.getPost().getId());
        }

        return ids;
    }

    private String switchKeyName(String key) {
        switch (key) {
            case "likes":
                System.out.println("1111");
                return "post.likeCounts";
            case "createTime":
                return "post.createdAt";
            case "views":
                return "post.viewCounts";
        }
        return key;
    }
}
