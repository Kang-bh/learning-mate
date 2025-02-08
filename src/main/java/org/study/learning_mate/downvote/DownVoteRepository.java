package org.study.learning_mate.downvote;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DownVoteRepository extends JpaRepository<DownVote, Long> {
    Page<DownVote> findAllByPost_Id(Long postId, Pageable pageable);
}
