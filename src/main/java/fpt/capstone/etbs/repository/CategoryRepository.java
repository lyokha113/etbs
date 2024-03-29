package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.Category;
import fpt.capstone.etbs.model.Template;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

  Optional<Category> getByName(String name);

  Optional<Category> getByNameAndIdNot(String name, Integer id);

  List<Category> getByIdIn(List<Integer> ids);

  List<Category> getByActiveTrue();

  List<Category> getByTemplatesIn(List<Template> templates);

}
