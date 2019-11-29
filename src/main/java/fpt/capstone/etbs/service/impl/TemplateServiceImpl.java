package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.Category;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.payload.TemplateCreateRequest;
import fpt.capstone.etbs.payload.TemplateListByCategories;
import fpt.capstone.etbs.payload.TemplateUpdateRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.CategoryRepository;
import fpt.capstone.etbs.repository.RawTemplateRepository;
import fpt.capstone.etbs.repository.TemplateRepository;
import fpt.capstone.etbs.service.FirebaseService;
import fpt.capstone.etbs.service.ImageGenerator;
import fpt.capstone.etbs.service.TemplateService;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplateServiceImpl implements TemplateService {

  @Autowired
  private TemplateRepository templateRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private RawTemplateRepository rawTemplateRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private FirebaseService firebaseService;

  @Autowired
  private ImageGenerator imageGenerator;

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
  public Template updateTemplate(Integer id, TemplateUpdateRequest request) throws Exception {

    Template template = templateRepository.findById(id).orElse(null);

    if (template == null) {
      throw new BadRequestException("Template doesn't exist");
    }

    Set<Category> categories = new HashSet<>(categoryRepository
        .getAllByActiveTrueAndIdIn(request.getCategoryIds()));

    template.setName(request.getName());
    template.setDescription(request.getDescription());
    template.setActive(request.isActive());
    template.setCategories(categories);
    return templateRepository.save(template);
  }

  @Override
  public Template createTemplate(TemplateCreateRequest request) {

    Account author = accountRepository.findById(request.getAuthorId()).orElse(null);
    if (author == null) {
      throw new BadRequestException("Author isn't existed");
    }

    Set<Category> categories = new HashSet<>(categoryRepository
        .getAllByActiveTrueAndIdIn(request.getCategoryIds()));

    Template template = Template.builder()
        .name(request.getName())
        .description(request.getDescription())
        .thumbnail(AppConstant.DEFAULT_RAW_TEMPLATE_THUMBNAIL)
        .content(request.getContent())
        .author(author)
        .active(true)
        .categories(categories)
        .build();

    return templateRepository.save(template);
  }

  @Override
  public Template updateContentImage(Template template) throws Exception {
    String content = template.getContent();
    Document doc = Jsoup.parse(content, "UTF-8");
    int count = 1;
    for (Element element : doc.select("img")) {
      String imgSrc = element.absUrl("src");
      if (imgSrc.contains(AppConstant.USER_IMAGES)) {
        String order = template.getId() + "/" + count;
        imgSrc = imgSrc.substring(0, imgSrc.indexOf("?"));
        String replace = firebaseService.createTemplateImagesFromUserImage(imgSrc, order);
        content = content.replaceAll(imgSrc, replace);
        count++;
      }
    }
    template.setContent(content);
    return templateRepository.save(template);
  }

  @Override
  public Template updateThumbnail(Template template) throws Exception {
    BufferedImage bufferedImage = imageGenerator.generateImageFromHtml(template.getContent());
    String url = firebaseService
        .createTemplateThumbnail(bufferedImage, String.valueOf(template.getId()));
      template.setThumbnail(url);
    return templateRepository.save(template);
  }

  @Override
  public void deleteTemplate(Integer id) {
    Template template = templateRepository.findById(id).orElse(null);

    if (template == null) {
      throw new BadRequestException("Template doesn't exist");
    }

    template.setCategories(null);
    templateRepository.delete(template);
  }

  @Override
  public List<Template> getHighRatingTemplate(int quantity) {
    return null;
  }

  @Override
  public List<Template> getListByCategories(TemplateListByCategories request) {
    List<Category> categories = new ArrayList<>(categoryRepository
        .getAllByActiveTrueAndIdIn(request.getCategories()));
    return templateRepository.findAllByCategoriesInAndActiveTrue(categories);
  }

}
