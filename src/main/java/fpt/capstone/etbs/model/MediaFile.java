package fpt.capstone.etbs.model;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Objects;
import java.util.UUID;

@Entity
@Data
@Table(name = "media_file")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaFile {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL)
    @NonNull
    private MediaStorage mediaStorage;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column(nullable = false)
    @NotBlank
    private String link;

    @Column(nullable = false)
    @NonNull
    private String type;

    @Override
    public String toString() {
        return "MediaFile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", link='" + link + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaFile mediaFile = (MediaFile) o;
        return Objects.equals(id, mediaFile.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
