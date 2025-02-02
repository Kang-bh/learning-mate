package org.study.learning_mate.demandlecture;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.study.learning_mate.user.User;

import java.util.Date;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "demand-lecture", schema = "learning_mate")
@EntityListeners(AuditingEntityListener.class)
public class DemandLecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name="title", nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", name = "content")
    private String content;

    @Column(name = "like_counts", nullable = false)
    @ColumnDefault("0")
    @Builder.Default()
    private Long likeCounts = 0L;

    @Column(name = "dislike_counts", nullable = false)
    @ColumnDefault("0")
    @Builder.Default()
    private Long dislikeCounts = 0L;

    @Column(name = "created_at")
    @CreatedDate
    private Date createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;
}
