package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, Integer> {
}
