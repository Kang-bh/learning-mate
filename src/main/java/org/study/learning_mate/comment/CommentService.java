package org.study.learning_mate.comment;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.study.learning_mate.dto.CommentDTO;
import org.study.learning_mate.post.Post;
import org.study.learning_mate.post.PostRepository;
import org.study.learning_mate.service.UserService;
import org.study.learning_mate.user.User;
import org.study.learning_mate.utils.CommentMapper;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostRepository postRepository;

    public CommentService(
            CommentRepository commentRepository,
            CommentMapper commentMapper,
            PostRepository postRepository
    ) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.postRepository = postRepository;
    }

    public Page<CommentDTO.CommentResponse> findAllCommentByPostId(Long postId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findAllByPost_Id(postId, pageable);

        return commentMapper.toCommentPageDTO(comments);
    }

    @Transactional
    public CommentDTO.CommentResponse createComment(CommentDTO.CommentRequest content, Long postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(NoSuchElementException::new);

        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .content(content.getContent())
                .build();

        Comment savedComment = commentRepository.save(comment);

        post.setCommentCounts(post.getCommentCounts() + 1);
        postRepository.save(post);

        return commentMapper.toCommentDTO(savedComment);
    }

    public CommentDTO.CommentResponse updateComment(String newContent, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            throw new NoSuchElementException("No such comment");
        });

        comment.setContent(newContent);
        Comment updatedContent = commentRepository.save(comment);
        return commentMapper.toCommentDTO(updatedContent);
    }


    @Transactional
    public void deleteComment(Long commentId, User user) throws AccessDeniedException {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            throw new NoSuchElementException("No such comment");
        });

        if (comment.getUser().getId() != user.getId()) {
            throw new AccessDeniedException("No Access to Comment");
        }

        Post post = comment.getPost();

        post.setCommentCounts(post.getCommentCounts() - 1);
        postRepository.save(post);

        commentRepository.deleteById(commentId);
        return;
    }
}
