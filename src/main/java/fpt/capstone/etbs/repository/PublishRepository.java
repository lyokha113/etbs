package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.constant.PublishStatus;
import fpt.capstone.etbs.model.Publish;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublishRepository extends JpaRepository<Publish, Integer> {

  List<Publish> getByAuthor_Id(UUID authorId);

  long countByAuthor_IdAndStatusInAndCreatedDateBetween(UUID authorId, List<PublishStatus> statuses, LocalDateTime from, LocalDateTime to);
}