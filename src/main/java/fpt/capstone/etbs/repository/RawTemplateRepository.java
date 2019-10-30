package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.RawTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RawTemplateRepository extends JpaRepository<RawTemplate, Integer> {
    List<RawTemplate> getAllByWorkspace_Id(int id);
}
