package org.study.learning_mate.lecture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.study.learning_mate.platform.PlatformTypeManager.PlatformType;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {
    Page<Lecture> findAllByPlatform(PlatformType platform, Pageable pageable);
    Page<Lecture> findAllByPost_TitleContainingAndPlatform(String title, PlatformType platform, Pageable pageable);
    Page<Lecture> findAllByPost_TitleContaining(String title, Pageable pageable);
}
