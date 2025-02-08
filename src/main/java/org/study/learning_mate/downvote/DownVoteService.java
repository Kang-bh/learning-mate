package org.study.learning_mate.downvote;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.study.learning_mate.dto.DownVoteDTO;
import org.study.learning_mate.post.Post;
import org.study.learning_mate.post.PostRepository;
import org.study.learning_mate.user.User;
import org.study.learning_mate.utils.DownVoteMapper;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DownVoteService {

    private final DownVoteRepository downVoteRepository;
    private final DownVoteMapper downVoteMapper;
    private final PostRepository postRepository;

    public DownVoteService(
            DownVoteRepository downVoteRepository,
            DownVoteMapper downVoteMapper,
            PostRepository postRepository
    ) {
        this.downVoteRepository = downVoteRepository;
        this.downVoteMapper = downVoteMapper;
        this.postRepository = postRepository;
    }

    public DownVote getDownVoteById(Long downVoteId) {
        return downVoteRepository.findById(downVoteId).orElseThrow(NoSuchElementException::new);
    }

    // downvote list 조회
    public List<DownVoteDTO.DownVoteResponse> getDownVotes(Long postId, Pageable pageable) {
        Page<DownVote> downVotes = downVoteRepository.findAllByPost_Id(postId, pageable);

        return downVoteMapper.toDownVoteListDTO(downVotes.stream().toList());
    }

    // downvote 생성
    public void createDownVote(Long postId, DownVoteDTO.DownVoteRequest request, User user) {

        Post post = postRepository.findById(postId)
                .orElseThrow(NoSuchElementException::new);

        DownVote downVote = DownVote.builder()
                .user(user)
                .post(post)
                .title(request.getTitle())
                .reason(request.getReason())
                .build();

        downVoteRepository.save(downVote);

        return;
    }

    // downvote 수정
    public DownVoteDTO.DownVoteResponse updateDownVote(Long downVoteId, DownVoteDTO.DownVoteRequest request) {

        // 유저 권한 확인
        DownVote downVote = downVoteRepository.findById(downVoteId).orElseThrow(NoSuchElementException::new);

        downVote.setTitle(request.getTitle());
        downVote.setReason(request.getReason());

        downVoteRepository.save(downVote);
        return downVoteMapper.toDownVoteDTO(downVote);
    }

    // downvote 삭제
    public void deleteDownVote(Long downVoteId, Long userId) throws AccessDeniedException {
        DownVote downVote = downVoteRepository.findById(downVoteId).orElseThrow(NoSuchElementException::new);

        if (userId !=  downVote.getUser().getId()) {
            throw new AccessDeniedException("No access");
        }

        downVoteRepository.delete(downVote);

        return;
    }
}
