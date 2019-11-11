package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.payload.TemplateCreateRequest;
import fpt.capstone.etbs.payload.TemplateUpdateRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface TemplateService {

  List<Template> getTemplates();

  List<Template> getActiveTemplates();

  Template getTemplate(Integer id);

  Template getActiveTemplate(Integer id);

  Template createTemplate(UUID accountId, TemplateCreateRequest request);

  Template updateTemplate(Integer id, TemplateUpdateRequest request) throws Exception;

  Template updateThumbnail(Template template, Integer rawTemplateId) throws Exception;

  void deleteTemplate(Integer id);

  List<Template> getHighRatingTemplate(int quantity);
}
