package fpt.capstone.etbs.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Data
@Table(name = "raw_template")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawTemplate  implements Serializable {

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

    @Override
    public String toString() {
        return "RawTemplate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", description='" + description + '\'' +
                ", workspace=" + workspace +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RawTemplate that = (RawTemplate) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
