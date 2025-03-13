package org.study.learning_mate.lecture;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;
import org.study.learning_mate.platform.PlatformTypeManager;
import org.study.learning_mate.post.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LectureSpecification {

    public static Specification<Lecture> filterBy(
            Optional<String> title,
            List<PlatformTypeManager.PlatformType> platforms
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Post와 Join
            Join<Lecture, Post> postJoin = root.join("post", JoinType.INNER);

            // 제목 필터링
            title.ifPresent(t -> predicates.add(
                    criteriaBuilder.like(postJoin.get("title"), "%" + t + "%"))
            );

            if (platforms != null && !platforms.isEmpty()) {
                List<Long> platformCodes = platforms.stream()
                        .map(PlatformTypeManager.PlatformType::getCode)
                        .collect(Collectors.toList());

                predicates.add(root.get("platform").get("id").in(platformCodes)); // WHERE platform_id IN (...)
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
