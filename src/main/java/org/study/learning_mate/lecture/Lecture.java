package org.study.learning_mate.lecture;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false)
    private Long id;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name = "upvote_counts", nullable = false)
    @ColumnDefault("0")
    @Builder.Default()
    private Long likeCounts = 0L;

    @Column(name = "downvote_counts", nullable = false)
    @ColumnDefault("0")
    @Builder.Default()
    private Long dislikeCounts = 0L;

    @Column(name="url", nullable = false)
    private String url;

    @Column(name = "created_at")
    @CreatedDate
    private Date createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;
}
