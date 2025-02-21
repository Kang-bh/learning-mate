package org.study.learning_mate.post;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.study.learning_mate.lecture.Lecture;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post", schema = "learning_mate")
@EntityListeners(AuditingEntityListener.class)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false)
    private Long id;

//    @Enumerated(PostType.STRING)
    @Column(name="post_type", nullable = false)
    private PostType postType;

    @Column(name="upvote_count", nullable = false)
    @ColumnDefault("0")
    @Builder.Default()
    private Long likeCounts = 0L;

    @Column(name="view_count", nullable = false)
    @ColumnDefault("0")
    @Builder.Default()
    private Long viewCounts = 0L;

    @Column(name="comment_count", nullable = false)
    @ColumnDefault("0")
    @Builder.Default()
    private Long commentCount = 0L;

    @Column(columnDefinition = "TEXT", name = "content")
    private String content;

    @Column(columnDefinition = "TEXT", name = "title")
    private String title;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name= "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private Lecture lecture;
}
