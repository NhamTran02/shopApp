package com.example.shopapp.controller;

import com.example.shopapp.DTO.CategoryDTO;
import com.example.shopapp.model.Category;
import com.example.shopapp.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Validated
@RequestMapping("${api.prefix}categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping("")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDTO categoryDTO, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getFieldErrors()
                    .stream().
                    map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessages.toString());
        }
        categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok().body("Insert category successfully");
    }

    @GetMapping("")
    public ResponseEntity<?> getAllCategories(@RequestParam("page") int page,
                                              @RequestParam("limit") int limit) {
        List<Category> categories=categoryService.getAllCategories();
        return ResponseEntity.ok().body(categories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO categoryDTO) {
        categoryService.updateCategory(id,categoryDTO);
        return ResponseEntity.ok().body("Update category successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().body("Delete category with id: "+id+" successfully" );
    }
}
