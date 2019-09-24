package fpt.capstone.etbs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fpt.capstone.etbs.component.Auditing;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Table(name = "media_storage")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdDate", "lastModifiedDate"}, allowGetters = true)
public class MediaStorage extends Auditing implements Serializable {

    @Id
    @Column
    @Type(type = "uuid-char")
    private UUID id;

    @OneToOne(cascade = CascadeType.ALL)
    @MapsId
    private Account account;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column(nullable = false)
    @NotBlank
    private String link;

    @Column(columnDefinition = "TINYINT(1) default 0", nullable = false)
    private boolean active;

    @OneToMany(mappedBy = "mediaStorage", cascade = CascadeType.ALL)
    private Set<MediaFile> files;

    @Override
    public String toString() {
        return "MediaStorage{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", link='" + link + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaStorage that = (MediaStorage) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
