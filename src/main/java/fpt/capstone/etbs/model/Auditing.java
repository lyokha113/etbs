package fpt.capstone.etbs.model;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
