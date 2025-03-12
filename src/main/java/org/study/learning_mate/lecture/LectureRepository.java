package org.study.learning_mate.lecture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.study.learning_mate.platform.PlatformTypeManager.PlatformType;

import java.util.List;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long>, JpaSpecificationExecutor<Lecture>  {
    Page<Lecture> findAllByPlatform(PlatformType platform, Pageable pageable);
    Page<Lecture> findAllByPost_TitleContainingAndPlatform(String title, PlatformType platform, Pageable pageable);
    Page<Lecture> findAllByPost_TitleContaining(String title, Pageable pageable);

    List<Lecture> findALlByPost_TitleContaining(String title);

    @Query("SELECT l FROM Lecture l WHERE l.id IN :postIds")
    List<Lecture> findByPostIds(@Param("postIds") List<Long> postIds);
}
