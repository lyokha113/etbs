package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.DeletingMediaFile;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeletingMediaFileRepository extends JpaRepository<DeletingMediaFile, UUID> {

}
