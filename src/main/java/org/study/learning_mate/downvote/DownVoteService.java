package org.study.learning_mate.downvote;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.study.learning_mate.dto.DownVoteDTO;
import org.study.learning_mate.lecture.Lecture;
import org.study.learning_mate.lecture.LectureRepository;
import org.study.learning_mate.post.Post;
import org.study.learning_mate.post.PostRepository;
import org.study.learning_mate.user.User;
import org.study.learning_mate.utils.DownVoteMapper;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class DownVoteService {

    private final DownVoteRepository downVoteRepository;
    private final DownVoteMapper downVoteMapper;
    private final PostRepository postRepository;
    private final LectureRepository lectureRepository;

    public DownVoteService(
            DownVoteRepository downVoteRepository,
            DownVoteMapper downVoteMapper,
            PostRepository postRepository,
            LectureRepository lectureRepository) {
        this.downVoteRepository = downVoteRepository;
        this.downVoteMapper = downVoteMapper;
        this.postRepository = postRepository;
        this.lectureRepository = lectureRepository;
    }

    public DownVote getDownVoteById(Long downVoteId) {
        return downVoteRepository.findById(downVoteId).orElseThrow(NoSuchElementException::new);
    }

    // downvote list 조회
    public Page<DownVoteDTO.DownVoteResponse> getDownVotes(Long postId, Pageable pageable) {
        List<Sort.Order> sortedOrders = pageable.getSort().stream()
                .map(order -> new Sort.Order(order.getDirection(), switchKeyName(order.getProperty())))
                .collect(Collectors.toList());

        Sort newSort = Sort.by(sortedOrders);

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);


        Page<DownVote> downVotes = downVoteRepository.findAllByPost_Id(postId, sortedPageable);

        return downVoteMapper.toDownVotePageDTO(downVotes);
    }

    // downvote 생성
    @Transactional
    public DownVoteDTO.DownVoteResponse createDownVote(Long postId, DownVoteDTO.DownVoteRequest request, User user) {

        Post post = postRepository.findById(postId)
                .orElseThrow(NoSuchElementException::new);

        DownVote downVote = DownVote.builder()
                .user(user)
                .post(post)
                .title(request.getTitle())
                .reason(request.getReason())
                .build();

        DownVote savedDownVote = downVoteRepository.save(downVote);

        Lecture lecture = post.getLecture();
        lecture.setDislikeCounts(lecture.getDislikeCounts() + 1);
        lectureRepository.save(lecture);

        return downVoteMapper.toDownVoteDTO(savedDownVote);
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
        System.out.println(11);
        if (userId !=  downVote.getUser().getId()) {
            throw new AccessDeniedException("No access");
        }

        System.out.println(2);
        downVoteRepository.delete(downVote);
        System.out.println(3);
        Lecture downVoteLecture = downVote.getPost().getLecture();
        System.out.println(4);
        downVoteLecture.setDislikeCounts(downVoteLecture.getDislikeCounts() - 1);
        lectureRepository.save(downVoteLecture);

        return;
    }

    private String switchKeyName(String key) {
        switch (key) {
            case "likes":
                return "likeCount";
            case "createTime":
                return "createdAt";
        }
        return key;
    }
}
