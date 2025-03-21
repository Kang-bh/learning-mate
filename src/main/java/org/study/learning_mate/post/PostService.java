package org.study.learning_mate.post;

import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class PostService {

    private PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post findById(Long postId) {
        return postRepository.findById(postId).orElseThrow(NoSuchElementException::new);
    }

    public void plusPostViewCount(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(NoSuchElementException::new);
        post.setViewCounts(post.getViewCounts() + 1);
        postRepository.save(post);
    }
}
