package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.Rating;
import fpt.capstone.etbs.model.RatingIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, RatingIdentity> {


}
