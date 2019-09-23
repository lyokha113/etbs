package fpt.capstone.etbs.service;

import fpt.capstone.etbs.payload.CategoryCreateRequest;
import fpt.capstone.etbs.payload.CategoryListResponse;
import fpt.capstone.etbs.payload.CategoryStatusRequest;
import fpt.capstone.etbs.payload.CategoryUpdateRequest;

import java.util.List;

public interface CategoryService {
    List<CategoryListResponse> getAllListCategory();
    void createCategory(CategoryCreateRequest request);
    void updateCategory(CategoryUpdateRequest request);
    void changeCategoryStatus(CategoryStatusRequest request);
}
