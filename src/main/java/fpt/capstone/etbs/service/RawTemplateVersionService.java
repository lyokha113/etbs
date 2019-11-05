package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.RawTemplateVersion;
import fpt.capstone.etbs.payload.RawTemplateVersionRequest;
import java.util.UUID;

public interface RawTemplateVersionService {

  RawTemplateVersion createVersion(UUID accountId, RawTemplateVersionRequest request);

  RawTemplateVersion updateVersion(UUID accountId, Integer id, RawTemplateVersionRequest request);

  void deleteVersion(UUID accountId, Integer id);
}
