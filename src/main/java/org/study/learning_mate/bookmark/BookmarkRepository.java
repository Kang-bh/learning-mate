package org.study.learning_mate.bookmark;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Page<Bookmark> findByUser_Id(Long userId, Pageable pageable);
    @Query("SELECT b " +
            "FROM Bookmark b " +
            "JOIN b.user u " +
            "JOIN b.post p " +
            "JOIN p.lecture l " +
            "JOIN l.platform pl " +
            "WHERE pl.title = :platformTitle AND u.id = :userId")
    Page<Bookmark> findBookmarksByPlatformTitleAndUserId(@Param("platformTitle") String platformTitle,
                                                         @Param("userId") Long userId,
                                                         Pageable pageable);
    Boolean existsByUser_IdAndPost_Id(Long userId, Long postId);
    void deleteBookmarkByUser_IdAndPost_Id(Long userId, Long postId);
}
