package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.payload.TemplateCreateRequest;
import fpt.capstone.etbs.payload.TemplateListByCategories;
import fpt.capstone.etbs.payload.TemplateUpdateRequest;
import java.util.List;

public interface TemplateService {

  List<Template> getTemplates();

  List<Template> getActiveTemplates();

  Template getTemplate(Integer id);

  Template getActiveTemplate(Integer id);

  Template createTemplate(TemplateCreateRequest request);

  Template updateTemplate(Integer id, TemplateUpdateRequest request) throws Exception;

  Template updateThumbnail(Template template) throws Exception;

  Template updateContentImage(Template template) throws Exception;

  void deleteTemplate(Integer id);

  List<Template> getHighRatingTemplate(int quantity);

  List<Template> getListByCategories(TemplateListByCategories request);
}
