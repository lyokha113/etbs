package fpt.capstone.etbs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Table(name = "account")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column
    @Type(type = "uuid-char")
    private UUID id;

    @Column(unique = true, nullable = false, updatable = false)
    @NotBlank
    private String email;

    @Column
    @JsonIgnore
    private String password;

    @Column(columnDefinition = "TINYINT(1) default 0", nullable = false)
    private boolean active;

    @Column(nullable = false)
    @NotBlank
    private String fullName;

    @ManyToOne
    @NotNull
    private Role role;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private Set<Rating> ratings;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private Set<Workspace> workspaces;

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", active=" + active +
                ", fullName='" + fullName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
