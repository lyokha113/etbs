package fpt.capstone.etbs.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import fpt.capstone.etbs.constant.AuthProvider;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@Table(name = "account")
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
public class Account extends Auditing {

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column
  @Type(type = "uuid-char")
  private UUID id;

  @Column(unique = true, nullable = false, updatable = false)
  @NotBlank
  private String email;

  @Column
  private String password;

  @Column(columnDefinition = "text")
  private String imageUrl;

  @Column(columnDefinition = "TINYINT(1) default 1")
  private boolean active;

  @Column(columnDefinition = "TINYINT(1) default 1")
  private boolean approved;

  @Column(nullable = false)
  @NotBlank
  private String fullName;

  @ManyToOne
  @NotNull
  private Role role;

  @Enumerated(EnumType.STRING)
  private AuthProvider provider;

  @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
  private Set<MediaFile> files;

  @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
  private Set<Rating> ratings;

  @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
  private Set<Workspace> workspaces;

  @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
  private Set<Publish> publishes;

  @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
  private Set<UserEmail> userEmails;

}
