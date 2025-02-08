package org.study.learning_mate.like;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeUpVoteRepository extends JpaRepository<LikeUpVote, Long> {
    LikeUpVote findByUser_IdAndUpVote_Id(Long userId, Long upvoteId);
    Boolean existsByUser_IdAndUpVote_Id(Long userId, Long upvoteId);
    void deleteByUser_IdAndUpVote_Id(Long userId, Long upvoteId);
}
