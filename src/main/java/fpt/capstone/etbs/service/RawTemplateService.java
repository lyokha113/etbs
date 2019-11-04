package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.payload.RawTemplateCreateRequest;
import fpt.capstone.etbs.payload.RawTemplateUpdateRequest;
import java.util.UUID;

public interface RawTemplateService {

  RawTemplate getRawTemplate(Integer id, UUID accountId);

  RawTemplate createRawTemplate(UUID accountId, RawTemplateCreateRequest request);

  RawTemplate updateRawTemplate(UUID accountId, Integer id, RawTemplateUpdateRequest request)
      throws Exception;

  RawTemplate changeVersion(UUID accountId, Integer id, Integer versionId);

  void deleteRawTemplate(UUID accountId, Integer id);
}
