package fpt.capstone.etbs.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

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
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
public class Rating extends Auditing {

  @EmbeddedId RatingIdentity id;

  @ManyToOne
  @MapsId("account_id")
  @JoinColumn(name = "account_id")
  private Account account;

  @ManyToOne
  @MapsId("template_id")
  @JoinColumn(name = "template_id")
  private Template template;

  @Column(nullable = false)
  private boolean vote;

  @Column(columnDefinition = "TINYINT(1) default 1")
  private boolean active;
}
