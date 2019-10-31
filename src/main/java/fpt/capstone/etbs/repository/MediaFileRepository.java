package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, UUID> {

  Optional<MediaFile> getByIdAndAccount_Id(UUID id, UUID accountId);

  List<MediaFile> getByAccount_Id(UUID accountId);

  List<MediaFile> getByActiveFalse();
}
