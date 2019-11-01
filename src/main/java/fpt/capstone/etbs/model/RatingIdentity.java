package fpt.capstone.etbs.model;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@Embeddable
public class RatingIdentity implements Serializable {

  @Column(name = "account_id")
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Type(type = "uuid-char")
  UUID accountId;

  @Column(name = "template_id")
  Integer templateId;
}
