package fpt.capstone.etbs.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@Table(name = "default_template")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefaultTemplate implements Serializable {

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
                ", content='" + content + '\'' +
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