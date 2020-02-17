package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.UserEmail;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEmailRepository extends JpaRepository<UserEmail, Integer> {

  Optional<UserEmail> getByToken(String token);

  List<UserEmail> getAllByAccount_Id(UUID accountId);
}
