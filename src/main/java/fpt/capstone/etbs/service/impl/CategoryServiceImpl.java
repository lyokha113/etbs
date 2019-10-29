package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Category;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.model.Workspace;
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
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TemplateRepository templateRepository;

    @Override
    public Category getCategory(int id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }


    @Override
    public Category createCategory(CategoryCreateRequest request) {

        if (isDuplicateName(request.getName()))
            throw new BadRequestException("Workspace name is existed");

        Category category = Category.builder()
                .name(request.getName())
                .active(true)
                .build();

        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(int id, CategoryUpdateRequest request) {

        Category category = categoryRepository.findById(id).orElse(null);

        if (category == null) throw new BadRequestException("Category doesn't exist");
        if (isDuplicateName(request.getName(), id))
            throw new BadRequestException("Category name is existed");

        category.setName(request.getName());
        category.setActive(request.isActive());
        return categoryRepository.save(category);

    }

    private boolean isDuplicateName(String name) {
        return categoryRepository.getByName(name).isPresent();
    }

    private boolean isDuplicateName(String name, Integer categoryId) {
        return categoryRepository.getByNameAndIdNot(name, categoryId).isPresent();
    }

}
