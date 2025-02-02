package org.study.learning_mate;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "refresh-token", schema = "learning_mate")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="refresh_token_id", nullable = false)
    private Long id;

    @Column(name="userId")
    private Long userId;

    @Column(name="refresh_token")
    private String refreshToken;

    @Column(name="expiration")
    private String expiration;

    public RefreshToken() {

    }
}
