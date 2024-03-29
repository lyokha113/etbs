package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.Category;
import fpt.capstone.etbs.payload.CategoryRequest;
import java.util.List;

public interface CategoryService {

  List<Category> getCategories();

  List<Category> getActiveCategories();

  Category createCategory(CategoryRequest request);

  Category updateCategory(int id, CategoryRequest request);
}
