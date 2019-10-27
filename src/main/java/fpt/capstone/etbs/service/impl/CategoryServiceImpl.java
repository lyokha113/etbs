package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.model.Category;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.payload.CategoryCreateRequest;
import fpt.capstone.etbs.payload.CategoryResponse;
import fpt.capstone.etbs.payload.CategoryUpdateRequest;
import fpt.capstone.etbs.payload.TemplatesOfCategory;
import fpt.capstone.etbs.repository.CategoryRepository;
import fpt.capstone.etbs.repository.TemplateRepository;
import fpt.capstone.etbs.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    TemplateRepository templateRepository;

    @Override
    public Category getCategory(int id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public List<CategoryResponse> getAllListCategory() {
        List<CategoryResponse> responses = new ArrayList<>();
        List<Category> categoryList = categoryRepository.findAll();
        return setValueToResponse(responses, categoryList);
    }

    private List<CategoryResponse> setValueToResponse(List<CategoryResponse> responses, List<Category> categoryList) {
        for (Category category : categoryList) {
            CategoryResponse temp = new CategoryResponse();
            temp.setId(category.getId());
            temp.setName(category.getName());
            List<Template> templateList = templateRepository.getAllByCategories(category);
            List<TemplatesOfCategory> list = new ArrayList<>();
            for (Template template : templateList) {
                list.add(new TemplatesOfCategory(template.getId(), template.getName(), template.getThumpnail(), template.getContent(), template.getDescription()));
            }
            temp.setTemplates(list);
            responses.add(temp);
        }
        return responses;
    }

    @Override
    public Category createCategory(CategoryCreateRequest request) {
        Category category = categoryRepository.getByName(request.getName()).orElse(null);
        if (category == null) {
            category = new Category();
            category.setName(request.getName());
            category.setActive(true);
            categoryRepository.save(category);
            return category;
        }
        return null;
    }

    @Override
    public Category updateCategory(int id, CategoryUpdateRequest request) {
        Category category = getCategory(id);
        if (category != null) {
            category.setName(request.getName());
            category.setActive(request.isActive());
            categoryRepository.save(category);
            return category;
        }
        return null;
    }

}
