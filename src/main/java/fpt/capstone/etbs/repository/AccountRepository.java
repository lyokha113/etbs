package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository  extends JpaRepository<Account, UUID> {
    Optional<Account> getByEmail(String email);
}