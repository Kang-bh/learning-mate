package org.study.learning_mate.downvote;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.study.learning_mate.lecture.Lecture;
import org.study.learning_mate.post.Post;
import org.study.learning_mate.user.User;

import java.util.Date;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "DownVote", schema = "learning_mate")
@EntityListeners(AuditingEntityListener.class)
public class DownVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name="title", nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", name = "reason")
    private String reason;

    @Column(name="like_count", nullable = false)
    @ColumnDefault("0")
    @Builder.Default()
    private Long likeCount = 0L;

    @Column(name = "created_at")
    @CreatedDate
    private Date createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;
}