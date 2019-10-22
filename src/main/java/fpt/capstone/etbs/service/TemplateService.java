package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.payload.TemplateCreateRequest;
import fpt.capstone.etbs.payload.TemplateUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface TemplateService {
    Template createTemplate(TemplateCreateRequest request);
    Template getTemplate(int id);
    Template updateTemplate(int id, TemplateUpdateRequest request);
    List<Template> getListTemplate(UUID id);
    List<Template> getAllTemplate();
}
