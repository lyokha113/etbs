package fpt.capstone.etbs.controller;

import fpt.capstone.etbs.model.Category;
import fpt.capstone.etbs.payload.*;
import fpt.capstone.etbs.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/category")
    private ResponseEntity<ApiResponse> getCategories() {
        List<CategoryResponse> categoryResponse = categoryService.getAllListCategory();
        return ResponseEntity.ok(new ApiResponse<>(true, "", categoryResponse));
    }

    @PostMapping("/category")
    private ResponseEntity<ApiResponse> createCategory(
            @Valid @RequestBody CategoryCreateRequest request) {

        Category category = categoryService.createCategory(request);
        return category != null ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "Category created", category)) :
                ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Category name is duplicated", null));
    }

    @PutMapping("/category/{id}")
    private ResponseEntity<ApiResponse> updateCategory(
            @PathVariable("id") int id,
            @Valid @RequestBody CategoryUpdateRequest request) {
        Category category = categoryService.updateCategory(id, request);
        return category != null ?
                ResponseEntity.ok(
                        new ApiResponse<>(true, "Update successful", category)) :
                ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Update failed. Not found", null));
    }


}
