package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.payload.RawTemplateRequest;
import java.util.UUID;

public interface RawTemplateService {

  RawTemplate getRawTemplate(Integer id, UUID accountId);

  RawTemplate createRawTemplate(UUID accountId, RawTemplateRequest request) throws Exception;

  RawTemplate updateRawTemplate(UUID accountId, Integer id, RawTemplateRequest request);

  RawTemplate changeVersion(UUID accountId, Integer id, Integer versionId);

  void deleteRawTemplate(UUID accountId, Integer id) throws Exception;
}
