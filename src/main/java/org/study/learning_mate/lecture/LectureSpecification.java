package org.study.learning_mate.lecture;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;
import org.study.learning_mate.platform.PlatformTypeManager;
import org.study.learning_mate.post.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LectureSpecification {

    public static Specification<Lecture> filterBy(
            Optional<String> title,
            Optional<PlatformTypeManager.PlatformType> platform
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Post와 Join
            Join<Lecture, Post> postJoin = root.join("post", JoinType.INNER);

            title.ifPresent(t -> predicates.add(criteriaBuilder.like(postJoin.get("title"), "%" + t + "%")));
            // PlatformType의 code를 기준으로 필터링
            platform.ifPresent(p -> predicates.add(criteriaBuilder.equal(root.get("platform").get("id"), p.getCode())));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
