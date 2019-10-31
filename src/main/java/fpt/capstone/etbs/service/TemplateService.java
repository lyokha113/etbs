package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.payload.TemplateCreateRequest;
import fpt.capstone.etbs.payload.TemplateUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface TemplateService {

  List<Template> getTemplates();

  List<Template> getActiveTemplates();

  Template getTemplate(Integer id);

  Template getActiveTemplate(Integer id);

  Template createTemplate(UUID accountId, TemplateCreateRequest request);

  Template updateTemplate(UUID accountId, Integer id, TemplateUpdateRequest request);

  List<Template> getHighRatingTemplate(int quantity);
}
