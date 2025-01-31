package org.study.learning_mate;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "User", schema = "learning-mate")
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @Column(name = "id",  nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(columnDefinition = "TEXT", name = "profile_image")
    private String profileImage;

    @Column(name = "email")
    private String email;

    @Column(name = "userId")
    private String userId;

    @Column(name = "password")
    private String password;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name= "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "TEXT", name = "background_image")
    private String backgroundImage;

    public void update(String profileImage, String nickname, String description, String birth, String password) {
        this.profileImage = profileImage;
    }

    public String getRoleKey() {
        return getRoleKey();
    }

    public void ifPresent(Object o) {
    }

    public void updateRefreshToken(String reIssuedRefreshToken) {

    }
}