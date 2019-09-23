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
    public void createCategory(CategoryCreateRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        categoryRepository.save(category);
    }

    @Override
    public boolean updateCategory(CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(request.getId()).orElse(null);
        if (category != null) {
            category.setName(request.getName());
            categoryRepository.save(category);
            return true;
        }
        return false;
    }

    @Override
    public void changeCategoryStatus(CategoryStatusRequest request) {
        Category category = categoryRepository.getOne(request.getId());
        category.setActive(request.isActive());
        categoryRepository.save(category);
    }


}
