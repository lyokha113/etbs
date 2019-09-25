package fpt.capstone.etbs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fpt.capstone.etbs.component.Auditing;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@Table(name = "default_template")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdDate", "lastModifiedDate"}, allowGetters = true)
public class DefaultTemplate extends Auditing implements Serializable {

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

    @Column(columnDefinition = "TINYINT(1) default 0", nullable = false)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultTemplate that = (DefaultTemplate) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
