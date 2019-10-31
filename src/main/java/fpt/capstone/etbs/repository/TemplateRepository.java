package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.Category;
import fpt.capstone.etbs.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Integer> {

  Optional<Template> getByIdAndActiveTrue(Integer id);

  List<Template> getByActiveTrue();

  List<Template> getAllByAuthor_Id(UUID id);

  List<Template> getAllByCategories(Category category);
}
