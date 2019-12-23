package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Category;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.payload.CategoryRequest;
import fpt.capstone.etbs.repository.CategoryRepository;
import fpt.capstone.etbs.service.CategoryService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

  @Autowired
  private CategoryRepository categoryRepository;

  @Override
  public List<Category> getCategories() {
    return categoryRepository.findAll();
  }

  @Override
  public List<Category> getActiveCategories() {
    return categoryRepository.getAllByActiveTrue();
  }

  @Override
  public Category createCategory(CategoryRequest request) {

    if (isDuplicateName(request.getName())) {
      throw new BadRequestException("Workspace name is existed");
    }

    Category category = Category.builder().name(request.getName()).active(true).build();

    return categoryRepository.save(category);
  }

  @Override
  public Category updateCategory(int id, CategoryRequest request) {

    Category category = categoryRepository.findById(id).orElse(null);

    if (category == null) {
      throw new BadRequestException("Category doesn't exist");
    }
    if (isDuplicateName(request.getName(), id)) {
      throw new BadRequestException("Category name is existed");
    }

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
