package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.Category;
import fpt.capstone.etbs.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Integer> {
    List<Template> findAllByAuthor_Id(UUID id);
    List<Template> getAllByCategories(Category category);
}
