package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.UserBlock;
import fpt.capstone.etbs.model.Workspace;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBlockRepository extends JpaRepository<UserBlock, Integer> {

  List<UserBlock> getByAccount_Id(UUID accountId);

  Optional<UserBlock> getByNameAndAccount_IdAndIdNot(String name, UUID accountId, Integer id);

  Optional<UserBlock> getByNameAndAccount_Id(String name, UUID accountId);

  Optional<UserBlock> getByAccount_IdAndId(UUID accountId, Integer id);
}
