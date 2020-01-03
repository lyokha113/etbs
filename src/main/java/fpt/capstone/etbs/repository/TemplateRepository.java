package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.Template;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Integer> {

  Optional<Template> getByName(String name);

  Optional<Template> getByNameAndIdNot(String name, Integer id);

  List<Template> getAllByAuthor_Id(UUID id);

}
