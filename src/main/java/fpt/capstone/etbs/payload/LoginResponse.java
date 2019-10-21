package fpt.capstone.etbs.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private UUID id;
    private String email;
    private String fullName;
    private String imageUrl;
    private int roleId;

    public LoginResponse(Account account) {
        this.id = account.getId();
        this.email = account.getEmail();
        this.fullName = account.getFullName();
        this.imageUrl = account.getImageUrl();
        this.roleId = account.getRole().getId();
    }


}
