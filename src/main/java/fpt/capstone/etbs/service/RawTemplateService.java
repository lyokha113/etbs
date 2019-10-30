package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.payload.RawTemplateCreateRequest;
import fpt.capstone.etbs.payload.RawTemplateCreateResponse;
import fpt.capstone.etbs.payload.RawTemplateResponse;

import java.util.List;

public interface RawTemplateService {
    RawTemplateCreateResponse createTemplate(RawTemplateCreateRequest request);
    List<RawTemplateResponse> getTemplateByWorkspaceId(int wId);
    RawTemplate getRawTemplateById(int id);
}
