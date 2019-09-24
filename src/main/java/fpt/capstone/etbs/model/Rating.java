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
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Data
@Table(name = "rating")
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private DefaultTemplate template;

    @Column(nullable = false)
    private boolean vote;

    @Column(columnDefinition = "TINYINT(1) default 0", nullable = false)
    private boolean active;

    @Override
    public String toString() {
        return "Rating{" +
                "id=" + id +
                ", vote=" + vote +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rating rating = (Rating) o;
        return Objects.equals(id, rating.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

