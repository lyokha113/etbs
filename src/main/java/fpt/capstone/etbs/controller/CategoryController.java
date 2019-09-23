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
        List<CategoryListResponse> categoryListResponses = categoryService.getAllListCategory();
        return ResponseEntity.ok(new ApiResponse<>(1, categoryListResponses));
    }

    @PostMapping("/category")
    private ResponseEntity<ApiResponse> createCategory(
            @Valid @RequestBody CategoryCreateRequest request) {
        categoryService.createCategory(request);
        return ResponseEntity.ok(new ApiResponse<>(1, "Create Successful"));
    }

    @PutMapping("/category/update")
    private ResponseEntity<ApiResponse> updateCategory(
            @Valid @RequestBody CategoryUpdateRequest request) {
        try {
            if (categoryService.updateCategory(request)) {
                return ResponseEntity.ok(new ApiResponse<>(1, "Update Successful"));
            } else {
                return ResponseEntity.ok(new ApiResponse<>(0, "Update Failed. Not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(-1, e.getMessage()));
        }
    }

    @PutMapping("/category/status")
    private ResponseEntity<ApiResponse> activateCategory(
            @Valid @RequestBody CategoryStatusRequest request) {
        categoryService.changeCategoryStatus(request);
        return ResponseEntity.ok(new ApiResponse<>(1, "Update Status Successful"));
    }

}
