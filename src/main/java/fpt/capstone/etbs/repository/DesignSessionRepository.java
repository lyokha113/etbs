package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.DesignSession;
import fpt.capstone.etbs.model.DesignSessionIdentity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignSessionRepository extends
    JpaRepository<DesignSession, DesignSessionIdentity> {

  List<DesignSession> getByRawTemplate_Workspace_Account_Id(UUID ownerId);

  List<DesignSession> getByRawTemplate_Workspace_Account_IdAndId_RawId(UUID ownerId, Integer rawId);

  List<DesignSession> getById_ContributorId(UUID contributorId);

  Optional<DesignSession> getById_ContributorIdAndId_RawId(UUID contributorId, Integer rawId);

  Optional<DesignSession> getByContributor_EmailAndId_RawId(String contributorEmail, Integer rawId);

}
