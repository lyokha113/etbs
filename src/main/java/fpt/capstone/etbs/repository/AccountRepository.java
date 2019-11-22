package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.Account;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

  Optional<Account> getByEmail(String email);

  List<Account> getByRole_Id(Integer roleId);
}
