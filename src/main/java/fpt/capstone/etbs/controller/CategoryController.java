package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.component.AuthenticationFacade;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Category;
import fpt.capstone.etbs.payload.ApiResponse;
import fpt.capstone.etbs.payload.CategoryCreateRequest;
import fpt.capstone.etbs.payload.CategoryResponse;
import fpt.capstone.etbs.payload.CategoryUpdateRequest;
import fpt.capstone.etbs.service.CategoryService;
import fpt.capstone.etbs.util.RoleUtils;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController {

  @Autowired
  private CategoryService categoryService;

  @Autowired
  private AuthenticationFacade authenticationFacade;

  @GetMapping("/category")
  private ResponseEntity<ApiResponse> getCategories() {

    Authentication auth = authenticationFacade.getAuthentication();
    List<Category> categories = RoleUtils.hasAdminRole(auth)
        ? categoryService.getCategories()
        : categoryService.getActiveCategories();

    List<CategoryResponse> response =
        categories.stream().map(CategoryResponse::setResponse).collect(Collectors.toList());
    return ResponseEntity.ok(new ApiResponse<>(true, "", response));
  }

  @PostMapping("/category")
  private ResponseEntity<ApiResponse> createCategory(
      @Valid @RequestBody CategoryCreateRequest request) {
    try {
      Category category = categoryService.createCategory(request);
      CategoryResponse response = CategoryResponse.setResponse(category);
      return ResponseEntity.ok(new ApiResponse<>(true, "Category created", response));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }

  @PutMapping("/category/{id}")
  private ResponseEntity<ApiResponse> updateCategory(
      @PathVariable("id") int id, @Valid @RequestBody CategoryUpdateRequest request) {
    try {
      Category category = categoryService.updateCategory(id, request);
      CategoryResponse response = CategoryResponse.setResponse(category);
      return ResponseEntity.ok(new ApiResponse<>(true, "Category updated", response));
    } catch (BadRequestException ex) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }
  }
}
