package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.EmailProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailProviderRepository extends JpaRepository<EmailProvider, Integer> {

}
