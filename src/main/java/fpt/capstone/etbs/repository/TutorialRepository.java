package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.Tutorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TutorialRepository  extends JpaRepository<Tutorial, Integer> {

    List<Tutorial> getByActiveTrue();
    Optional<Tutorial> getByIdAndActiveTrue(Integer id);
}
