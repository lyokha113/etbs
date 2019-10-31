package fpt.capstone.etbs.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import fpt.capstone.etbs.constant.AuthProvider;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

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

  @Column private String password;

  @Column private String imageUrl;

  @Column(columnDefinition = "TINYINT(1) default 1")
  private boolean active;

  @Column(nullable = false)
  @NotBlank
  private String fullName;

  @ManyToOne @NotNull private Role role;

  @Enumerated(EnumType.STRING)
  private AuthProvider provider;

  @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
  private Set<MediaFile> files;

  @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
  private Set<Rating> ratings;

  @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
  private Set<Workspace> workspaces;
}
