package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.RawTemplateVersion;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawTemplateVersionRepository extends JpaRepository<RawTemplateVersion, Integer> {

  Optional<RawTemplateVersion> getByIdAndTemplate_IdAndTemplate_Workspace_Account_Id(Integer id,
      Integer templateId, UUID accountId);

  Optional<RawTemplateVersion> getByIdAndTemplate_Workspace_Account_Id(Integer id, UUID accountId);

  Optional<RawTemplateVersion> getByNameAndTemplate_Id(String name, Integer templateId);

  Optional<RawTemplateVersion> getByNameAndTemplate_IdAndIdNot(String name, Integer templateId,
      Integer id);

}
