package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.Notification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

  List<Notification> getByAccount_IdOrderByCreatedDateDesc(UUID accountId);

  List<Notification> getByAccount_IdAndLoadedFalseOrderByCreatedDateDesc(UUID accountId);

  List<Notification> getByLoadedTrueAndCreatedDateBefore(LocalDateTime limit);

  List<Notification> getByIdIn(Integer [] ids);
}
