package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.RawTemplateVersion;
import fpt.capstone.etbs.payload.RawTemplateVersionRequest;
import java.util.UUID;

public interface RawTemplateVersionService {

  RawTemplateVersion createVersion(UUID accountId, RawTemplateVersionRequest request);

  RawTemplateVersion updateVersion(UUID accountId, RawTemplateVersionRequest request);

  RawTemplateVersion updateContent(UUID accountId, Integer rawTemplateId, String content) throws Exception;

  void deleteVersion(UUID accountId, Integer id) throws Exception;
}
