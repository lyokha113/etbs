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
@Table(name = "raw_template")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(value = {"createdDate", "lastModifiedDate"}, allowGetters = true)
public class RawTemplate extends Auditing implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column(columnDefinition = "longtext")
    @NotBlank
    private String content;

    @Column(columnDefinition = "text")
    private String description;

    @ManyToOne
    @NonNull
    private Workspace workspace;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL)
    private Set<RawTemplateVersion> versions;

    @Column(columnDefinition = "TINYINT(1) default 1")
    private boolean active;

    @Override
    public String toString() {
        return "RawTemplate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", workspace=" + workspace +
                '}';
    }

}
