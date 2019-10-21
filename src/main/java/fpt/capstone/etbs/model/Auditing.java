package fpt.capstone.etbs.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class Auditing {
    @CreatedDate
    @Column(name = "created_date")
    @Temporal(TIMESTAMP)
    protected Date createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    @Temporal(TIMESTAMP)
    protected Date lastModifiedDate;
}
