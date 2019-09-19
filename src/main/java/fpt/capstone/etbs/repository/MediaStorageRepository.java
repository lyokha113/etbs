package fpt.capstone.etbs.repository;

import fpt.capstone.etbs.model.MediaStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MediaStorageRepository extends JpaRepository<MediaStorage, UUID> {
}
