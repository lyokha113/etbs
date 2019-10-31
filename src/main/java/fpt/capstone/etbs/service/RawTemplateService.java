package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.model.Workspace;
import fpt.capstone.etbs.payload.RawTemplateCreateRequest;
import fpt.capstone.etbs.payload.RawTemplateResponse;
import fpt.capstone.etbs.payload.WorkspaceRequest;
import java.util.List;
import java.util.UUID;

public interface RawTemplateService {

  RawTemplate getRawTemplate(Integer id, UUID accountId);

  RawTemplate createRawTemplate(UUID accountId, RawTemplateCreateRequest request);

//  RawTemplate updateRawTemplate(Integer workspaceId, Integer id, RawTemplateUpdateRequest request);
//
//  void deleteWorkspace(UUID accountId, Integer workspaceId);
}
