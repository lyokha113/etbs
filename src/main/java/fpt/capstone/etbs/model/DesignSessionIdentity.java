package fpt.capstone.etbs.model;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Builder
@EqualsAndHashCode(of = {"contributorId", "rawTemplateId"}, callSuper = false)
@EntityListeners(AuditingEntityListener.class)
@ToString(of = {"contributorId", "rawTemplateId"})
public class DesignSessionIdentity implements Serializable {

  @Column(name = "contributor_id")
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Type(type = "uuid-char")
  private UUID contributorId;

  @Column(name = "raw_template_id")
  private Integer rawTemplateId;
}
