package org.study.learning_mate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "RefreshToken", schema = "learning_mate")
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
}
