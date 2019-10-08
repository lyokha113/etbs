package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefaultTemplateRepository extends JpaRepository<Template, Integer> {
}
