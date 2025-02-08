package org.study.learning_mate.upvote;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UpVoteRepository extends JpaRepository<UpVote, Long> {
    Page<UpVote> findAllByPost_Id(Long postId, Pageable pageable);
}
