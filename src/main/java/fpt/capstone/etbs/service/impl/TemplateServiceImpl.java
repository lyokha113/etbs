package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.payload.TemplateCreateRequest;
import fpt.capstone.etbs.payload.TemplateUpdateRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.CategoryRepository;
import fpt.capstone.etbs.repository.RatingRepository;
import fpt.capstone.etbs.repository.TemplateRepository;
import fpt.capstone.etbs.service.TemplateService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplateServiceImpl implements TemplateService {

  @Autowired
  TemplateRepository templateRepository;
  @Autowired
  AccountRepository accountRepository;
  @Autowired
  CategoryRepository categoryRepository;
  @Autowired
  RatingRepository ratingRepository;

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
    return null;
  }

  @Override
  public Template updateTemplate(UUID accountId, Integer id, TemplateUpdateRequest request) {
    return null;
  }

  @Override
  public List<Template> getHighRatingTemplate(int quantity) {
    return null;
  }

  //    @Override
  //    public TemplateCreateResponse createTemplate(TemplateCreateRequest request) {
  //        if (accountRepository.findById(request.getAuthor()).isPresent()) {
  //            Template template = new Template();
  //            template.setActive(true);
  //            template.setVote(0);
  //            template.setName(request.getName());
  //            template.setAuthor(Account.builder().id(request.getAuthor()).build());
  //            template.setContent(request.getContent());
  //            template.setThumbnail("");
  //            template.setDescription(request.getDescription());
  //            Set<Category> categories = new HashSet<>();
  //            for (int i = 0; i < request.getCategories().size(); i++) {
  //                if (categoryRepository.findById(request.getCategories().get(i)).isPresent()) {
  //
  // categories.add(Category.builder().id(request.getCategories().get(i)).build());
  //                }
  //            }
  //            template.setCategories(categories);
  //            templateRepository.save(template);
  //            TemplateCreateResponse response = new TemplateCreateResponse();
  //            response.setId(template.getId());
  //            response.setCategories(request.getCategories());
  //            response.setThumbnail(template.getThumbnail());
  //            return response;
  //        }
  //      }
  //      templateRepository.save(template);
  //      TemplateCreateResponse response = new TemplateCreateResponse();
  //      response.setId(template.getId());
  //      response.setCategories(request.getCategories());
  //      response.setThumpnail(template.getThumbnail());
  //      return response;
  //    }
  //    return null;
  //  }
  //
  //  @Override
  //  public TemplateResponse getTemplate(int id) {
  //    if (templateRepository.findById(id).isPresent()) {
  //      Template template = templateRepository.findById(id).get();
  //      TemplateResponse temp = new TemplateResponse();
  //      temp.setThumbnail(template.getThumbnail());
  //      List<Category> categoryList = categoryRepository.getAllByTemplates(template);
  //      List<CategoriesOfTemplate> list = new ArrayList<>();
  //      for (Category category : categoryList) {
  //        list.add(new CategoriesOfTemplate(category.getId(),
  //            category.getName()));
  //      }
  //      temp.setContent(template.getContent());
  //      temp.setDescription(template.getDescription());
  //      temp.setCategories(list);
  //      temp.setName(template.getName());
  //    }
  //    return null;
  //  }
  //
  //    @Override
  //    public boolean updateTemplate(int id, TemplateUpdateRequest request) {
  //        if (templateRepository.findById(id).isPresent()) {
  //            Template template = templateRepository.findById(id).get();
  //            template.setActive(request.isActive());
  //            template.setName(request.getName());
  //            template.setAuthor(Account.builder().id(request.getAuthor()).build());
  //            template.setContent(request.getContent());
  //            template.setThumbnail(request.getThumbnail());
  //            template.setDescription(request.getDescription());
  //            for (int i = 0; i < request.getCategories().size(); i++) {
  //                if (categoryRepository.findById(request.getCategories().get(i)).isPresent()) {
  //
  // template.getCategories().add(Category.builder().id(request.getCategories().get(i)).build());
  //                }
  //            }
  //            templateRepository.save(template);
  //            return true;
  //        }
  //      }
  //      templateRepository.save(template);
  //      return true;
  //    }
  //    return false;
  //  }
  //
  //  @Override
  //  public List<TemplateResponse> getListTemplate(UUID id) {
  //    List<TemplateResponse> responses = new ArrayList<>();
  //    List<Template> templateList = templateRepository.getAllByAuthor_Id(id);
  //    return setValueToResponse(responses, templateList);
  //  }
  //
  //    @Override
  //    public List<TemplateResponse> getAllListTemplate() {
  //        List<TemplateResponse> responses = new ArrayList<>();
  //        List<Template> templateList = templateRepository.findAll();
  //        return setValueToResponse(responses, templateList);
  //    }
  //
  //    @Override
  //    public List<TemplateResponse> getHighRatingTemplate(int quantity) {
  //
  //        return null;
  //    }
  //
  //    private List<TemplateResponse> setValueToResponse(List<TemplateResponse> responses,
  // List<Template> templateList) {
  //        for (Template template : templateList) {
  //            TemplateResponse temp = new TemplateResponse();
  //            List<Category> categoryList = categoryRepository.getAllByTemplates(template);
  //            List<CategoriesOfTemplate> list = new ArrayList<>();
  //            for (Category category : categoryList) {
  //                list.add(new CategoriesOfTemplate(category.getId(),
  //                        category.getName()));
  //            }
  //            temp.setContent(template.getContent());
  //            temp.setDescription(template.getDescription());
  //            temp.setCategories(list);
  //            temp.setThumbnail(template.getThumbnail());
  //            temp.setName(template.getName());
  //            responses.add(temp);
  //        }
  //        return responses;
  //    }
  //    return responses;
  //  }
}
