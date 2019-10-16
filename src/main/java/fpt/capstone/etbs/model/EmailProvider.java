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
@Table(name = "email_provider")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(value = {"createdDate", "lastModifiedDate"}, allowGetters = true)
public class EmailProvider extends Auditing implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column
    private String setting;

    @Column(columnDefinition = "TINYINT(1) default 1")
    private boolean active;

    @Override
    public String toString() {
        return "EmailProvider{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", setting='" + setting + '\'' +
                '}';
    }

}
