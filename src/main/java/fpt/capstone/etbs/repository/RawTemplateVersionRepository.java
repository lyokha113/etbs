package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.RawTemplateVersion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawTemplateVersionRepository extends JpaRepository<RawTemplateVersion, Integer> {

  List<RawTemplateVersion> getByTemplate_Id(Integer rawTemplateId);

}
