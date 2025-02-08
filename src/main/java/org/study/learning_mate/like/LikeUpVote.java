package org.study.learning_mate.like;


import jakarta.persistence.*;
import lombok.*;
import org.study.learning_mate.upvote.UpVote;
import org.study.learning_mate.user.User;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "like_upvote", schema = "learning_mate")
public class LikeUpVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="upvote_id", nullable = false)
    private UpVote upVote;
}
