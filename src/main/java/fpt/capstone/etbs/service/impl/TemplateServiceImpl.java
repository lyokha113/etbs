package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.model.Category;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.payload.TemplateCreateRequest;
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
    public Template createTemplate(TemplateCreateRequest request) {
        if (accountRepository.findById(request.getAuthor()).isPresent()) {
            Template template = new Template();
            template.setActive(true);
            template.setAuthor(accountRepository.findById(request.getAuthor()).get());
            template.setContent(request.getContent());
            template.setDescription(request.getDescription());
            Set<Category> categoryList = new HashSet<>();
            for (int i = 0; i < request.getCategories().size(); i++) {
                if (categoryRepository.findById(request.getCategories().get(i)).isPresent()) {
                    categoryList.add(categoryRepository.findById(request.getCategories().get(i)).get());
                }
            }
            template.setCategories(categoryList);
            return template;
        }
        return null;
    }

    @Override
    public Template getTemplate(int id) {
        if (templateRepository.findById(id).isPresent()) {
            return templateRepository.findById(id).get();
        }
        return null;
    }

    @Override
    public Template updateTemplate(int id, TemplateUpdateRequest request) {
        Template template = getTemplate(id);
        if (template != null) {
            template.setActive(request.isActive());
            template.setAuthor(accountRepository.findById(request.getAuthor()).get());
            template.setContent(request.getContent());
            template.setDescription(request.getDescription());
            Set<Category> categoryList = new HashSet<>();
            for (int i = 0; i < request.getCategories().size(); i++) {
                if (categoryRepository.findById(request.getCategories().get(i)).isPresent()) {
                    categoryList.add(categoryRepository.findById(request.getCategories().get(i)).get());
                }
            }
            template.setCategories(categoryList);
            return template;
        }
        return null;
    }

    @Override
    public List<Template> getListTemplate(UUID id) {
//        if (accountRepository.findById(id).isPresent()) {
//            List<Template> templateList = templateRepository.getAllByAuthor_Id(id);
//            if (!templateList.isEmpty()) {
//                return templateList;
//            }
//        }
        return null;
    }

    @Override
    public List<Template> getAllTemplate() {
        //TODO: get only high rating template
        return templateRepository.findAll();
    }
}
