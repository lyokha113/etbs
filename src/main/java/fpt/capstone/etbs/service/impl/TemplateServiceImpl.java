package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Category;
import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.payload.TemplateCreateRequest;
import fpt.capstone.etbs.payload.TemplateUpdateRequest;
import fpt.capstone.etbs.repository.CategoryRepository;
import fpt.capstone.etbs.repository.RawTemplateRepository;
import fpt.capstone.etbs.repository.TemplateRepository;
import fpt.capstone.etbs.service.FirebaseService;
import fpt.capstone.etbs.service.TemplateService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TemplateServiceImpl implements TemplateService {

  @Autowired
  private TemplateRepository templateRepository;

  @Autowired
  private RawTemplateRepository rawTemplateRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private FirebaseService firebaseService;

  @Override
  public List<Template> getTemplates() {
    return templateRepository.findAll();
  }

  @Override
  public List<Template> getActiveTemplates() {
    return templateRepository.getByActiveTrue();
  }

  @Override
  public Template getTemplate(Integer id) {
    return templateRepository.findById(id).orElse(null);
  }

  @Override
  public Template getActiveTemplate(Integer id) {
    return templateRepository.getByIdAndActiveTrue(id).orElse(null);
  }

  @Override
  public Template createTemplate(UUID accountId, TemplateCreateRequest request) {

    RawTemplate rawTemplate = rawTemplateRepository
        .getByIdAndWorkspace_Account_Id(request.getRawTemplateId(), accountId)
        .orElse(null);

    if (rawTemplate == null) {
      throw new BadRequestException("Raw template doesn't exist");
    }

    // Process publish stage
    Set<Category> categories = new HashSet<>(categoryRepository
        .getAllByActiveTrueAndIdIn(request.getCategoryIds()));

    Template template = Template.builder()
        .name(request.getName())
        .description(request.getDescription())
        .thumbnail(AppConstant.DEFAULT_RAW_TEMPLATE_THUMBNAIL)
        .content(rawTemplate.getCurrentVersion().getContent())
        .author(rawTemplate.getWorkspace().getAccount())
        .active(true)
        .categories(categories)
        .build();

    return templateRepository.save(template);
  }

  @Override
  public Template updateTemplate(Integer id, TemplateUpdateRequest request) throws Exception {

    Template template = templateRepository.findById(id).orElse(null);

    if (template == null) {
      throw new BadRequestException("Template doesn't exist");
    }

    Set<Category> categories = new HashSet<>(categoryRepository
        .getAllByActiveTrueAndIdIn(request.getCategoryIds()));
    String thumbnail = firebaseService.createTemplateThumbnail(request.getThumbnail(), id.toString());

    template.setName(request.getName());
    template.setContent(request.getContent());
    template.setThumbnail(thumbnail);
    template.setDescription(request.getDescription());
    template.setActive(request.isActive());
    template.setCategories(categories);
    return templateRepository.save(template);
  }

  @Override
  public Template updateThumbnail(Template template, MultipartFile thumbnail) throws Exception {
    String link = firebaseService
        .createTemplateThumbnail(thumbnail, template.getId().toString());

    template.setThumbnail(link);
    return templateRepository.save(template);
  }

  @Override
  public void deleteTemplate(Integer id) {
    Template template = templateRepository.findById(id).orElse(null);

    if (template == null) {
      throw new BadRequestException("Template doesn't exist");
    }

    templateRepository.delete(template);
    // Process publish data
  }

  @Override
  public List<Template> getHighRatingTemplate(int quantity) {
    return null;
  }

}
