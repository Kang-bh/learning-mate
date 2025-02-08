package org.study.learning_mate.demandlecture;

import jakarta.persistence.*;
import lombok.*;
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
@Table(name = "demand_lecture", schema = "learning_mate")
@EntityListeners(AuditingEntityListener.class)
public class DemandLecture {

    @Id
    @EmbeddedId
    private DemandLecturePK demandLecturePK;

    @Column(name = "created_at")
    @CreatedDate
    private Date createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

    public Long getPostId() {
        return this.demandLecturePK.getPost().getId();
    }

}
