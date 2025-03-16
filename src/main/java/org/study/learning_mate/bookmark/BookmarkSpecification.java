package org.study.learning_mate.bookmark;


import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.study.learning_mate.lecture.Lecture;
import org.study.learning_mate.platform.Platform;
import org.study.learning_mate.platform.PlatformTypeManager;
import org.study.learning_mate.post.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookmarkSpecification {

    public static Specification<Bookmark> filterBy(
            Long userId, // 사용자 ID는 필수
            List<PlatformTypeManager.PlatformType> platforms // 플랫폼 리스트는 선택
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // User 필터링 (필수 조건)
            predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));

            // Post와 Lecture 및 Platform 조인
            Join<Bookmark, Post> postJoin = root.join("post", JoinType.INNER);
            Join<Post, Lecture> lectureJoin = postJoin.join("lecture", JoinType.INNER);
            Join<Lecture, Platform> platformJoin = lectureJoin.join("platform", JoinType.INNER);

            if (platforms != null && !platforms.isEmpty()) {
                List<Long> platformCodes = platforms.stream()
                        .map(PlatformTypeManager.PlatformType::getCode) // PlatformType에서 code 추출
                        .collect(Collectors.toList());

                predicates.add(platformJoin.get("id").in(platformCodes)); // WHERE platform.id IN (...)
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
