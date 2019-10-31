package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.RawTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RawTemplateRepository extends JpaRepository<RawTemplate, Integer> {

  Optional<RawTemplate> getByIdAndWorkspace_Account_Id(Integer id, UUID accountId);

  Optional<RawTemplate> getByNameAndWorkspace_Id(String name, Integer workspaceId);
}
