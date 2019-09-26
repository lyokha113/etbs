package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Category;
import fpt.capstone.etbs.payload.CategoryCreateRequest;
import fpt.capstone.etbs.payload.CategoryListResponse;
import fpt.capstone.etbs.payload.CategoryStatusRequest;
import fpt.capstone.etbs.payload.CategoryUpdateRequest;

import java.util.List;

public interface CategoryService {
    List<CategoryListResponse> getAllListCategory();
    Category getCategory(int id);
    Category createCategory(CategoryCreateRequest request);
    Category updateCategory(CategoryUpdateRequest request);
    Category changeCategoryStatus(CategoryStatusRequest request);
}
