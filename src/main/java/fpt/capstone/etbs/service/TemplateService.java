package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.payload.TemplateRequest;
import java.util.List;
import java.util.UUID;

public interface TemplateService {

  List<Template> getTemplates();

  List<Template> getTemplatesForUser();

  List<Template> getTemplatesByAuthor(UUID uuid);

  Template getTemplate(Integer id);

  Template createTemplate(TemplateRequest request) throws Exception;

  Template updateTemplate(Integer id, TemplateRequest request) throws Exception;

  Template updateThumbnail(Template template) throws Exception;

  Template updateContentImage(Template template) throws Exception;

  void deleteTemplate(Integer id) throws Exception;

  void calculateScore();

}
