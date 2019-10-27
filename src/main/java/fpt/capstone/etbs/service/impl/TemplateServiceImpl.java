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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public TemplateCreateResponse createTemplate(TemplateCreateRequest request) {
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
    public Template getTemplate(int id) {
        return templateRepository
                .findById(id).orElse(null);
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
        return templateRepository
                .findAllByAuthor_Id(id)
                .stream()
                .map(TemplateResponse::setResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TemplateResponse> getAllListTemplate() {
        return templateRepository
                .findAll()
                .stream()
                .map(TemplateResponse::setResponse)
                .collect(Collectors.toList());
    }
}
