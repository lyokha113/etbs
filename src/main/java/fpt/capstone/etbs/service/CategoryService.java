package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Category;
import fpt.capstone.etbs.payload.CategoryCreateRequest;
import fpt.capstone.etbs.payload.CategoryUpdateRequest;
import java.util.List;

public interface CategoryService {

  List<Category> getCategories();

  List<Category> getActiveCategories();

  Category createCategory(CategoryCreateRequest request);

  Category updateCategory(int id, CategoryUpdateRequest request);
}
