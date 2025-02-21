package org.study.learning_mate.demandlecture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DemandLectureRepository extends JpaRepository<DemandLecture, DemandLecturePK> {
    @Query("SELECT dl FROM DemandLecture dl WHERE dl.demandLecturePK.post.id = :postId")
    DemandLecture findByPostId(@Param("postId") Long postId);

    @Query("SELECT dl FROM DemandLecture dl WHERE dl.demandLecturePK.user.id = :userId")
    Page<DemandLecture> findByUserId(@Param("userId") Long userId, Pageable pageable);
}

