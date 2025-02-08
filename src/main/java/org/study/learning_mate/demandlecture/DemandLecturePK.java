package org.study.learning_mate.demandlecture;

import jakarta.persistence.*;
import lombok.*;
import org.study.learning_mate.post.Post;
import org.study.learning_mate.user.User;

import java.io.Serializable;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DemandLecturePK implements Serializable {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;
}
