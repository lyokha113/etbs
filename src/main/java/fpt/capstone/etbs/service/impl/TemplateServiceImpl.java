package fpt.capstone.etbs.service.impl;

import static fpt.capstone.etbs.constant.AppConstant.PIVOT_DATE;
import static fpt.capstone.etbs.constant.AppConstant.TIME_TO_SCORE;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.Category;
import fpt.capstone.etbs.model.DeletingMediaFile;
import fpt.capstone.etbs.model.Publish;
import fpt.capstone.etbs.model.Rating;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.payload.TemplateRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.CategoryRepository;
import fpt.capstone.etbs.repository.DeletingMediaFileRepository;
import fpt.capstone.etbs.repository.PublishRepository;
import fpt.capstone.etbs.repository.RatingRepository;
import fpt.capstone.etbs.repository.TemplateRepository;
import fpt.capstone.etbs.service.FirebaseService;
import fpt.capstone.etbs.service.ImageGeneratorService;
import fpt.capstone.etbs.service.TemplateService;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TemplateServiceImpl implements TemplateService {

  @Autowired
  private TemplateRepository templateRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private PublishRepository publishRepository;

  @Autowired
  private RatingRepository ratingRepository;

  @Autowired
  private DeletingMediaFileRepository deletingMediaFileRepository;

  @Autowired
  private FirebaseService firebaseService;

  @Autowired
  private ImageGeneratorService imageGeneratorService;

  @Override
  public List<Template> getTemplates() {
    return templateRepository.findAll();
  }

  @Override
  public List<Template> getTemplatesForUser() {
    List<Template> templates = templateRepository.findAll();
    return templates.stream()
        .filter(t -> t.getCategories().stream().anyMatch(Category::isActive))
        .collect(Collectors.toList());
  }

  @Override
  public List<Template> getTemplatesByAuthor(UUID uuid) {
    List<Template> templates = templateRepository.getByAuthor_Id(uuid);
    return templates.stream()
        .filter(t -> t.getCategories().stream().anyMatch(Category::isActive))
        .collect(Collectors.toList());
  }

  @Override
  public Template getTemplate(Integer id) {
    return templateRepository.findById(id).orElse(null);
  }

  @Override
  public Template updateTemplate(Integer id, TemplateRequest request) {

    Template template = templateRepository.findById(id).orElse(null);

    if (template == null) {
      throw new BadRequestException("Template doesn't exist");
    }

    if (isDuplicateName(request.getName(), template.getId())) {
      throw new BadRequestException("Template name is existed");
    }

    Set<Category> categories = new HashSet<>(
        categoryRepository.getByIdIn(request.getCategoryIds()));

    template.setName(request.getName());
    template.setDescription(request.getDescription());
    template.setCategories(categories);
    return templateRepository.save(template);
  }

  @Override
  public Template createTemplate(TemplateRequest request) throws Exception {

    Account author = accountRepository.findById(request.getAuthorId()).orElse(null);
    if (author == null) {
      throw new BadRequestException("Author isn't existed");
    }

    if (isDuplicateName(request.getName())) {
      throw new BadRequestException("Template name is existed");
    }

    Set<Category> categories = new HashSet<>(
        categoryRepository.getByIdIn(request.getCategoryIds()));

    Template template = Template.builder()
        .name(request.getName())
        .description(request.getDescription())
        .thumbnail(AppConstant.DEFAULT_RAW_TEMPLATE_THUMBNAIL)
        .content(request.getContent())
        .author(author)
        .categories(categories)
        .build();

    template = templateRepository.save(template);
    template = updateContentImage(template);
    return template;
  }

  @Override
  public Template updateContentImage(Template template) throws Exception {
    String content = template.getContent();
    Document doc = Jsoup.parse(content, "UTF-8");
    int count = 1;
    for (Element element : doc.select("img")) {
      String src = element.absUrl("src");
      String order = template.getId() + "/" + count;
      String replace = "";
      if (src.startsWith("http://storage.googleapis.com/") && src.contains(AppConstant.USER_IMAGES)) {
        src = src.substring(0, src.indexOf("?"));
        replace = firebaseService.createTemplateImages(src, order);
      } else {
        replace = firebaseService.createTemplateImages(new URL(src), order);
      }
      content = content.replaceAll(src, replace);
      count++;
    }
    template.setContent(content);
    return templateRepository.save(template);
  }


  @Transactional
  @Override
  public void deleteTemplate(Integer id) throws Exception {
    Template template = templateRepository.findById(id).orElse(null);

    if (template == null) {
      throw new BadRequestException("Template doesn't exist");
    }

    List<DeletingMediaFile> files = new ArrayList<>();
    Document doc = Jsoup.parse(template.getContent(), "UTF-8");
    for (Element element : doc.select("img")) {
      String src = element.absUrl("src");
      if (src.startsWith("http://storage.googleapis.com/") && src.contains(AppConstant.TEMPLATE_IMAGE)) {
        String link = src.substring(src.indexOf(AppConstant.TEMPLATE_IMAGE), src.indexOf("?"));
        files.add(DeletingMediaFile.builder().link(link).build());
      }
    }
    files.add(DeletingMediaFile.builder().link(AppConstant.TEMPLATE_IMAGE + id).build());
    firebaseService.deleteImage(AppConstant.TEMPLATE_THUMBNAIL + id);
    deletingMediaFileRepository.saveAll(files);

    List<Publish> publishes = publishRepository
        .getByDuplicateTemplate_Id(template.getId());
    publishes.forEach(p -> p.setDuplicateTemplate(null));
    publishRepository.saveAll(publishes);

    template.setCategories(null);
    templateRepository.delete(template);
  }

  @Override
  public Template updateThumbnail(Template template) throws Exception {
    BufferedImage bufferedImage = imageGeneratorService
        .generateImageFromHtml(template.getContent());
    String url = firebaseService
        .createTemplateThumbnail(bufferedImage, String.valueOf(template.getId()));
    template.setThumbnail(url);
    return templateRepository.save(template);
  }

  @Override
  @Async("calculateScoreAsyncExecutor")
  public void calculateScore() {
    List<Template> templates = templateRepository.findAll();
    for (Template template : templates) {

      List<Rating> ratings = ratingRepository.getByTemplate_Id(template.getId());
      long up = ratings.stream().filter(Rating::isVote).count();
      long down = ratings.stream().filter(r -> !r.isVote()).count();
      long sign = up - down;
      double voteScore = Math.log((Math.max(Math.abs(sign), 1))) / Math.log(2);
      sign = Long.compare(sign, 0);

      long pivot = LocalDateTime.parse(PIVOT_DATE).toEpochSecond(ZoneOffset.UTC);
      List<Category> categories = categoryRepository.getByTemplatesIn(
          Collections.singletonList(template));
      boolean isTrending = categories.stream().anyMatch(c -> c.isTrending() && c.isActive());
      long timeElapsed = isTrending ?
          LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - pivot :
          template.getCreatedDate().toEpochSecond(ZoneOffset.UTC) - pivot;
      double timeScore = (double) (sign * timeElapsed) / TIME_TO_SCORE;

      BigDecimal score = new BigDecimal(voteScore + timeScore)
          .setScale(6, BigDecimal.ROUND_CEILING);
      template.setScore(score.doubleValue());
    }

    templateRepository.saveAll(templates);
  }

  private boolean isDuplicateName(String name) {
    return templateRepository.getByName(name).isPresent();
  }

  private boolean isDuplicateName(String name, Integer id) {
    return templateRepository.getByNameAndIdNot(name, id).isPresent();
  }
}
