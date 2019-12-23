package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.RawTemplateVersion;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawTemplateVersionRepository extends JpaRepository<RawTemplateVersion, Integer> {


  Optional<RawTemplateVersion> getByIdAndRawTemplate_Workspace_Account_Id(Integer id,
      UUID accountId);

  Optional<RawTemplateVersion> getByNameAndRawTemplate_IdAndIdNot(String name, Integer templateId,
      Integer id);

}
