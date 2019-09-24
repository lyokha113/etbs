package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.model.Category;
import fpt.capstone.etbs.payload.CategoryCreateRequest;
import fpt.capstone.etbs.payload.CategoryListResponse;
import fpt.capstone.etbs.payload.CategoryStatusRequest;
import fpt.capstone.etbs.payload.CategoryUpdateRequest;
import fpt.capstone.etbs.repository.CategoryRepository;
import fpt.capstone.etbs.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public List<CategoryListResponse> getAllListCategory() {
        List<CategoryListResponse> resultList = new ArrayList<>();
        List<Category> categoryList = categoryRepository.findAll();
        for (Category category : categoryList) {
            resultList.add(new CategoryListResponse(category.getId(), category.getName()));
        }
        return resultList;
    }

    @Override
    public Category getCategory(int id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public boolean createCategory(CategoryCreateRequest request) {
        Category category = categoryRepository.getByName(request.getName()).orElse(null);
        if (category == null) {
            category = new Category();
            category.setName(request.getName());
            categoryRepository.save(category);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateCategory(CategoryUpdateRequest request) {
        Category category = getCategory(request.getId());
        if (category != null) {
            category.setName(request.getName());
            categoryRepository.save(category);
            return true;
        }
        return false;
    }

    @Override
    public boolean changeCategoryStatus(CategoryStatusRequest request) {
        Category category = getCategory(request.getId());
        if (category != null) {
            category.setActive(request.isActive());
            categoryRepository.save(category);
            return true;
        }
        return false;
    }
}
