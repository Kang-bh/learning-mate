package org.study.learning_mate.platform;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.scheduling.support.SimpleTriggerContext;

import java.util.Date;


@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "platform", schema = "learning_mate")
@EntityListeners(AuditingEntityListener.class)
public class Platform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false)
    private Long id;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="logo_url", nullable = false)
    private String logoUrl;

    @Column(name="url", nullable = false)
    private String url;

    @Column(name="url_prefix", nullable = false)
    private String urlPrefix;

    @Column(name = "created_at")
    @CreatedDate
    private Date createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;
}
