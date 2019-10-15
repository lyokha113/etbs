package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.model.Category;
import fpt.capstone.etbs.payload.CategoryCreateRequest;
import fpt.capstone.etbs.payload.CategoryResponse;
import fpt.capstone.etbs.payload.CategoryUpdateRequest;
import fpt.capstone.etbs.repository.CategoryRepository;
import fpt.capstone.etbs.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public Category getCategory(int id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public List<CategoryResponse> getAllListCategory() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> new CategoryResponse(category.getId(), category.getName()))
                .collect(Collectors.toList());
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
