package fpt.capstone.etbs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@Table(name = "template")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(value = {"createdDate", "lastModifiedDate"}, allowGetters = true)
public class Template extends Auditing implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    @NotBlank
    private String name;

    @Column
    private Account author;

    @Column(columnDefinition = "longtext", nullable = false)
    @NotBlank
    private String content;

    @Column(columnDefinition = "text")
    private String description;

    @Column(columnDefinition = "TINYINT(1) default 1")
    private boolean active;

    @ManyToMany(mappedBy = "templates", cascade = CascadeType.ALL)
    private Set<Category> categories;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL)
    private Set<Rating> ratings;

    @Override
    public String toString() {
        return "DefaultTemplate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", author=" + author +
                ", description='" + description + '\'' +
                '}';
    }

}
