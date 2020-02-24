package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

  Optional<Category> getByName(String name);

  Optional<Category> getByNameAndIdNot(String name, Integer id);

  List<Category> getAllByIdIn(List<Integer> ids);

  List<Category> getByActiveTrue();

}
