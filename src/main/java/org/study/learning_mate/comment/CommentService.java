package org.study.learning_mate.comment;

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

    public List<CommentDTO.CommentResponse> findAllCommentByPostId(Long postId) {
        List<Comment> comments = commentRepository.findAllByPost_Id(postId);

        return commentMapper.toCommentListDTO(comments);
    }

    public void createComment(CommentDTO.CommentRequest content, Long postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(NoSuchElementException::new);

        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .content(content.getContent())
                .build();

        commentRepository.save(comment);
        return;
    }

    public CommentDTO.CommentResponse updateComment(String newContent, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            throw new NoSuchElementException("No such comment");
        });

        comment.setContent(newContent);
        Comment updatedContent = commentRepository.save(comment);
        return commentMapper.toCommentDTO(updatedContent);
    }

    public void deleteComment(Long commentId, User user) throws AccessDeniedException {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            throw new NoSuchElementException("No such comment");
        });

        if (comment.getUser().getId() != user.getId()) {
            throw new AccessDeniedException("No Access to Comment");
        }

        commentRepository.deleteById(commentId);
        return;
    }
}
