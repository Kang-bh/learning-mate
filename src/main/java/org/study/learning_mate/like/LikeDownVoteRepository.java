package org.study.learning_mate.like;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeDownVoteRepository extends JpaRepository<LikeDownVote, Integer> {
    LikeDownVote findByUser_IdAndDownVote_Id(Long userId, Long downVoteId);
    Boolean existsByUser_IdAndDownVote_Id(Long userId, Long downVoteId);
    void deleteByUser_IdAndDownVote_Id(Long userId, Long downVoteId);
}
