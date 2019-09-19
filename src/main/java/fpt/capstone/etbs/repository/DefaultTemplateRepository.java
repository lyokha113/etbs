package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.DefaultTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefaultTemplateRepository extends JpaRepository<DefaultTemplate, Integer> {
}
