package fpt.capstone.etbs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Objects;


@Entity
@Data
@Table(name = "raw_template_version")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(value = {"createdDate", "lastModifiedDate"}, allowGetters = true)
public class RawTemplateVersion extends Auditing implements Serializable {

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

    @ManyToOne
    @NonNull
    private RawTemplate template;

    @Column(columnDefinition = "TINYINT(1) default 1")
    private boolean active;

    @Override
    public String toString() {
        return "TemplateVersion{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", active=" + active +
                '}';
    }

}
