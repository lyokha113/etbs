package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.payload.RawTemplateCreateRequest;
import fpt.capstone.etbs.payload.RawTemplateCreateResponse;

public interface RawTemplateService {
    RawTemplateCreateResponse createTemplate(RawTemplateCreateRequest request);
}
