package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.payload.TemplateRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TemplateService {

  List<Template> getTemplates();

  List<Template> getActiveTemplates();

  Template getTemplate(Integer id);

  Template getActiveTemplate(Integer id);

  Template createTemplate(TemplateRequest request);

  Template updateTemplate(Integer id, TemplateRequest request) throws Exception;

  CompletableFuture<Template> updateThumbnailAsync(Template template) throws Exception;

  Template updateThumbnail(Template template) throws Exception;

  Template updateContentImage(Template template) throws Exception;

  void deleteTemplate(Integer id) throws Exception;

}
