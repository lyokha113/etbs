package fpt.capstone.etbs.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Data
@Table(name = "rating")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdDate", "lastModifiedDate"}, allowGetters = true)
public class Rating extends Auditing implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Account account;

    @ManyToOne
    private Template template;

    @Column(nullable = false)
    private boolean vote;

    @Column(columnDefinition = "TINYINT(1) default 1")
    private boolean active;

    @Override
    public String toString() {
        return "Rating{" +
                "id=" + id +
                ", vote=" + vote +
                '}';
    }

}

