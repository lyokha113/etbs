package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {
  Optional<Rating> getByIdAndTemplate_Id(Integer id, Integer templateId);
}
