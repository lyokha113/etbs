package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Integer> {

    Optional<Workspace> getByNameAndAccount_Id(String name, UUID accountId);
    Optional<Workspace> getByNameAndAccount_IdAndIdNot(String name, UUID accountId, Integer id);
    Optional<Workspace> getByIdAndAccount_Id(int workspaceId, UUID accountId);
    List<Workspace> getByAccount_Id(UUID accountID);

}
