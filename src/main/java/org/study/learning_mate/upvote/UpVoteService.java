package org.study.learning_mate.upvote;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.study.learning_mate.dto.UpVoteDTO;
import org.study.learning_mate.lecture.Lecture;
import org.study.learning_mate.post.Post;
import org.study.learning_mate.post.PostRepository;
import org.study.learning_mate.user.User;
import org.study.learning_mate.utils.UpVoteMapper;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UpVoteService {

    private final UpVoteRepository upVoteRepository;
    private final UpVoteMapper upVoteMapper;
    private final PostRepository postRepository;

    public UpVoteService(
            UpVoteRepository upVoteRepository,
            UpVoteMapper upVoteMapper,
            PostRepository postRepository
        ) {
        this.upVoteRepository = upVoteRepository;
        this.upVoteMapper = upVoteMapper;
        this.postRepository = postRepository;
    }

    public UpVote getUpVoteById(Long upVoteId) {
        return upVoteRepository.findById(upVoteId).orElseThrow(NoSuchElementException::new);
    }

    public Page<UpVoteDTO.UpVoteResponse> getUpVotes(Long postId, Pageable pageable) {
        Page<UpVote> upVotes = upVoteRepository.findAllByPost_Id(postId, pageable);
        return upVoteMapper.toUpVotePageResponse(upVotes);
    }

    public UpVoteDTO.UpVoteResponse createUpVote(
            Long postId,
            UpVoteDTO.UpVoteRequest request,
            User user
        ) {

        Post post = postRepository.findById(postId).orElseThrow(NoSuchElementException::new);

        UpVote upVote = UpVote.builder()
                .user(user)
                .post(post)
                .title(request.getTitle())
                .reason(request.getReason())
                .build();

        UpVote savedUpVote = upVoteRepository.save(upVote);


        post.setLikeCounts(post.getLikeCounts() + 1);
        postRepository.save(post);

        return upVoteMapper.toUpVoteResponse(savedUpVote);
    }

    public UpVoteDTO.UpVoteResponse updateUpVote(
            Long upVoteId,
            UpVoteDTO.UpVoteRequest request,
            Long userId
    ) throws AccessDeniedException {
        UpVote upVote = upVoteRepository.findById(upVoteId).orElseThrow(NoSuchElementException::new);

        if (upVote.getUser().getId() != userId) {
            throw new AccessDeniedException("No Access");
        }

        upVote.setTitle(request.getTitle());
        upVote.setReason(request.getReason());

        upVoteRepository.save(upVote);
        return upVoteMapper.toUpVoteResponse(upVote);
    }

    public void deleteUpVote(Long upVoteId, Long userId) throws AccessDeniedException {

        UpVote upVote = upVoteRepository.findById(upVoteId).orElseThrow(NoSuchElementException::new);

        if (upVote.getUser().getId() != userId) {
            throw new AccessDeniedException("No Access");
        }

        upVoteRepository.deleteById(upVoteId);
    }
}
