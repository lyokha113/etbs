package fpt.capstone.etbs.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Data
@Table(name = "template")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(of = {"id"})
@JsonIgnoreProperties(
    value = {"createdDate", "lastModifiedDate"},
    allowGetters = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
public class Template extends Auditing {

  @Id
  @Column
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  @NotBlank
  private String name;

  @ManyToOne private Account author;

  @Column(columnDefinition = "longtext", nullable = false)
  @NotBlank
  private String content;

  @Column(columnDefinition = "text")
  private String description;

  @Column(columnDefinition = "TINYINT(1) default 1")
  private boolean active;

  @Column(columnDefinition = "text")
  private String thumbnail;

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "category_templates",
      joinColumns = {@JoinColumn(name = "categories_id")},
      inverseJoinColumns = {@JoinColumn(name = "templates_id")})
  private Set<Category> categories;

  @OneToMany(mappedBy = "template", cascade = CascadeType.ALL)
  private Set<Rating> ratings;
}
