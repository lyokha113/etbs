package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.Category;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.payload.TemplateCreateRequest;
import fpt.capstone.etbs.payload.TemplateCreateResponse;
import fpt.capstone.etbs.payload.TemplateResponse;
import fpt.capstone.etbs.payload.TemplateUpdateRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.CategoryRepository;
import fpt.capstone.etbs.repository.TemplateRepository;
import fpt.capstone.etbs.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TemplateServiceImpl implements TemplateService {
    //TODO: add link thumpnail
    @Autowired
    TemplateRepository templateRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public TemplateCreateResponse
    createTemplate(TemplateCreateRequest request) {
        if (accountRepository.findById(request.getAuthor()).isPresent()) {
            Template template = new Template();
            template.setActive(true);
            template.setName(request.getName());
            template.setAuthor(Account.builder().id(request.getAuthor()).build());
            template.setContent(request.getContent());
            //TODO: add link for thumpnail
            template.setThumpnail("");
            template.setDescription(request.getDescription());
            for (int i = 0; i < request.getCategories().size(); i++) {
                if (categoryRepository.findById(request.getCategories().get(i)).isPresent()) {
                    template.getCategories().add(Category.builder().id(request.getCategories().get(i)).build());
                }
            }
            templateRepository.save(template);
            TemplateCreateResponse response = new TemplateCreateResponse();
            response.setId(template.getId());
            response.setCategories(request.getCategories());
            response.setThumpnail(template.getThumpnail());
            return response;
        }
        return null;
    }

    @Override
    public TemplateResponse getTemplate(int id) {
        if (templateRepository.findById(id).isPresent()) {
            Template template = templateRepository.findById(id).get();
            TemplateResponse response = new TemplateResponse();
            response.setThumpnail(template.getThumpnail());
            List<Category> categoryList = categoryRepository.getAllByTemplates(template);
            List<String> categoryNameList = new ArrayList<>();
            for (Category category : categoryList) {
                categoryNameList.add(category.getName());
            }
            response.setCategories(categoryNameList);
            response.setName(template.getName());
            return response;
        }
        return null;
    }

    @Override
    public boolean updateTemplate(int id, TemplateUpdateRequest request) {
        if (templateRepository.findById(id).isPresent()) {
            Template template = templateRepository.findById(id).get();
            template.setActive(request.isActive());
            template.setName(request.getName());
            template.setAuthor(Account.builder().id(request.getAuthor()).build());
            template.setContent(request.getContent());
            template.setThumpnail(request.getThumpnail());
            template.setDescription(request.getDescription());
            for (int i = 0; i < request.getCategories().size(); i++) {
                if (categoryRepository.findById(request.getCategories().get(i)).isPresent()) {
                    template.getCategories().add(Category.builder().id(request.getCategories().get(i)).build());
                }
            }
            templateRepository.save(template);
            return true;
        }
        return false;
    }

    @Override
    public List<TemplateResponse> getListTemplate(UUID id) {
        //TODO: get only high rating template
        List<TemplateResponse> responses = new ArrayList<>();
        List<Template> templateList = templateRepository.findAllByAuthor_Id(id);
        for (int i = 0; i < templateList.size(); i++) {
            TemplateResponse temp = new TemplateResponse();
            temp.setThumpnail(templateList.get(i).getThumpnail());
            List<Category> categoryList = categoryRepository.getAllByTemplates(templateList.get(i));
            List<String> categoryNameList = new ArrayList<>();
            for (int j = 0; j < categoryList.size(); j++) {
                categoryNameList.add(categoryList.get(j).getName());
            }
            temp.setCategories(categoryNameList);
            temp.setName(templateList.get(i).getName());
            responses.add(temp);
        }
        return responses;
    }

    @Override
    public List<TemplateResponse> getAllTemplate() {
        //TODO: get only high rating template
        List<TemplateResponse> responses = new ArrayList<>();
        List<Template> templateList = templateRepository.findAll();
        for (int i = 0; i < templateList.size(); i++) {
            TemplateResponse temp = new TemplateResponse();
            temp.setThumpnail(templateList.get(i).getThumpnail());
            List<Category> categoryList = categoryRepository.getAllByTemplates(templateList.get(i));
            List<String> categoryNameList = new ArrayList<>();
            for (int j = 0; j < categoryList.size(); j++) {
                categoryNameList.add(categoryList.get(j).getName());
            }
            temp.setCategories(categoryNameList);
            temp.setName(templateList.get(i).getName());
            responses.add(temp);
        }
        return responses;
    }
}
