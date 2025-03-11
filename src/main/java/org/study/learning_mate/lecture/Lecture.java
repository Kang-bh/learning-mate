package org.study.learning_mate.lecture;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.study.learning_mate.platform.Platform;
import org.study.learning_mate.post.Post;

import java.util.Date;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "lecture", schema = "learning_mate")
@EntityListeners(AuditingEntityListener.class)
public class Lecture {

    @Id
    @Column(name="id", nullable = false)
    private Long id;  // Post의 id와 동일한 값 사용

    @Column(name = "downvote_counts", nullable = false)
    @ColumnDefault("0")
    @Builder.Default()
    private Long dislikeCounts = 0L;

    @Column(name="url", nullable = false)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="platform_id")
    private Platform platform;

    @Column(name = "created_at")
    @CreatedDate
    private Date createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")  // Lecture의 기본 키(id)를 Post의 id와 동일하게 사용
    @MapsId
    private Post post;
}