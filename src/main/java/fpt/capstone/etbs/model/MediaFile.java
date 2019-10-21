package fpt.capstone.etbs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(value = {"createdDate", "lastModifiedDate"}, allowGetters = true)
public class MediaFile extends Auditing {

    @Id
    @Column
    private UUID id;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column(columnDefinition = "text", nullable = false)
    @NotBlank
    private String link;

    @ManyToOne
    @NonNull
    private Account account;

    @Column(columnDefinition = "TINYINT(1) default 1")
    private boolean active;

    @Override
    public String toString() {
        return "MediaFile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", link='" + link + '\'' +
                '}';
    }

}
