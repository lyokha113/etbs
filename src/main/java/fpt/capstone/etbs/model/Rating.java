package fpt.capstone.etbs.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@Table(name = "rating")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
@EntityListeners(AuditingEntityListener.class)
@ToString(of = {"id"})
@JsonIgnoreProperties(
    value = {"createdDate", "lastModifiedDate"},
    allowGetters = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Rating extends Auditing {

  @EmbeddedId
  private RatingIdentity id;

  @MapsId("account_id")
  @ManyToOne(optional = false)
  private Account account;

  @MapsId("template_id")
  @ManyToOne(optional = false)
  private Template template;

  @Column(nullable = false)
  private boolean vote;
}
