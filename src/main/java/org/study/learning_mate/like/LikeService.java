package org.study.learning_mate.like;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.study.learning_mate.UserRepository;
import org.study.learning_mate.downvote.DownVote;
import org.study.learning_mate.downvote.DownVoteRepository;
import org.study.learning_mate.post.Post;
import org.study.learning_mate.post.PostRepository;
import org.study.learning_mate.service.UserService;
import org.study.learning_mate.upvote.UpVote;
import org.study.learning_mate.upvote.UpVoteRepository;
import org.study.learning_mate.user.User;

import java.util.NoSuchElementException;

@Service
public class LikeService {

    private final LikePostRepository likePostRepository;
    private final LikeUpVoteRepository likeUpVoteRepository;
    private final LikeDownVoteRepository likeDownVoteRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final UpVoteRepository upVoteRepository;
    private final DownVoteRepository downVoteRepository;

    public LikeService(
            LikePostRepository likePostRepository,
            LikeUpVoteRepository likeUpVoteRepository,
            LikeDownVoteRepository likeDownVoteRepository,
            UserRepository userRepository,
            PostRepository postRepository,
            UpVoteRepository upVoteRepository,
            DownVoteRepository downVoteRepository) {
        this.likePostRepository = likePostRepository;
        this.likeUpVoteRepository = likeUpVoteRepository;
        this.likeDownVoteRepository = likeDownVoteRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.upVoteRepository = upVoteRepository;
        this.downVoteRepository = downVoteRepository;
    }

    public boolean checkLikeInPost(Long postId, Long userId) {
        boolean isExist = likePostRepository.existsByUser_IdAndPost_Id(userId, postId);
        return isExist;
    }

    public boolean checkLikeInUpVote(Long upVoteId, Long userId) {
        return likeUpVoteRepository.existsByUser_IdAndUpVote_Id(userId, upVoteId);
    }

    public boolean checkLikeInDownVote(Long downVoteId, Long userId) {
        return likeDownVoteRepository.existsByUser_IdAndDownVote_Id(userId, downVoteId);
    }

    public void createLikeInPost(Post post, User user) {
        LikePost likePost = LikePost.builder()
                .post(post)
                .user(user)
                .build();

        likePostRepository.save(likePost);

        post.setLikeCounts(post.getLikeCounts() + 1);
        postRepository.save(post);

        return;
    }

    @Transactional
    public void createLikeInUpVote(UpVote upVote, User user) {
        LikeUpVote likeUpVote = LikeUpVote.builder()
                .upVote(upVote)
                .user(user)
                .build();

        likeUpVoteRepository.save(likeUpVote);

        Post post = likeUpVote.getUpVote().getPost();
        post.setLikeCounts(post.getLikeCounts() + 1);

        postRepository.save(post);

        return;
    }

    @Transactional
    public void createLikeInDownVote(DownVote downVote, User user) {
        LikeDownVote likeDownVote = LikeDownVote.builder()
                .downVote(downVote)
                .user(user)
                .build();

        likeDownVoteRepository.save(likeDownVote);

        Post post = likeDownVote.getDownVote().getPost();
        post.setLikeCounts(post.getLikeCounts() + 1);

        postRepository.save(post);

        return;
    }

    // unlike
    @Transactional
    public void deleteLikeInPost(Long postId, Long userId) {
        // check access
        // delete
        likePostRepository.deleteByUser_IdAndPost_Id(userId, postId);

        Post post = postRepository.findById(postId).orElseThrow(NoSuchElementException::new);

        post.setLikeCounts(post.getLikeCounts() - 1);
        postRepository.save(post);
        return;
    }

    @Transactional
    public void deleteLikeInUpVote(Long userId, Long upVoteId) {
        // check access
        // delete
        likeUpVoteRepository.deleteByUser_IdAndUpVote_Id(userId, upVoteId);

        UpVote upVote = upVoteRepository.findById(upVoteId).orElseThrow(NoSuchElementException::new);
        Post post = upVote.getPost();
        post.setLikeCounts(post.getLikeCounts() - 1);

        postRepository.save(post);
        return;
    }

    @Transactional
    public void deleteLikeInDownVote(Long userId, Long downVoteId) {
        // check access
        // delete
        likeDownVoteRepository.deleteByUser_IdAndDownVote_Id(userId, downVoteId);


        DownVote downVote = downVoteRepository.findById(downVoteId).orElseThrow(NoSuchElementException::new);
        Post post = downVote.getPost();
        post.setLikeCounts(post.getLikeCounts() - 1);

        postRepository.save(post);
        return;
    }
}
