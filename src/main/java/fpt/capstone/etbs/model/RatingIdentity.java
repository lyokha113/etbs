package fpt.capstone.etbs.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@EqualsAndHashCode(of = {"accountId", "templateId"}, callSuper = false)
@EntityListeners(AuditingEntityListener.class)
@ToString(of = {"accountId", "templateId"})
public class RatingIdentity implements Serializable {

  @Column(name = "account_id")
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Type(type = "uuid-char")
  UUID accountId;

  @Column(name = "template_id")
  Integer templateId;
}