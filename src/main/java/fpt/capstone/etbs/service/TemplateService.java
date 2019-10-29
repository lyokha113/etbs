package fpt.capstone.etbs.service;

import fpt.capstone.etbs.payload.TemplateCreateRequest;
import fpt.capstone.etbs.payload.TemplateCreateResponse;
import fpt.capstone.etbs.payload.TemplateResponse;
import fpt.capstone.etbs.payload.TemplateUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface TemplateService {
    TemplateCreateResponse createTemplate(TemplateCreateRequest request);
    TemplateResponse getTemplate(int id);
    boolean updateTemplate(int id, TemplateUpdateRequest request);
    List<TemplateResponse> getListTemplate(UUID id);
    List<TemplateResponse> getAllListTemplate();
    List<TemplateResponse> getHighRatingTemplate(int quantity);
}
