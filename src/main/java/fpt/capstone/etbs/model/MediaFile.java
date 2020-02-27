package fpt.capstone.etbs.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@Table(name = "media_file")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(of = {"id"})
@JsonIgnoreProperties(
    value = {"createdDate", "lastModifiedDate"},
    allowGetters = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, property = "id")
public class MediaFile extends Auditing {

  @Id
  @Column
  @Type(type = "uuid-char")
  private UUID id;

  @Column(nullable = false)
  @NotBlank
  private String name;

  @Column(columnDefinition = "text", nullable = false)
  @NotBlank
  private String link;

  @Column(columnDefinition = "TINYINT(1) default 1")
  private boolean active;

  @Column(columnDefinition = "TINYINT(1) default 1")
  private boolean open;

  @ManyToOne(optional = false)
  @NonNull
  private Account account;
}
