package com.example.shopapp.services.impl;

import com.example.shopapp.DTO.CategoryDTO;
import com.example.shopapp.model.Category;

import java.util.List;

public interface CategoryServiceImpl {
    Category createCategory(CategoryDTO categoryDTO);
    Category getCategoryById(Long id);
    List<Category> getAllCategories();
    Category updateCategory(Long categoryId,CategoryDTO categoryDTO);
    void deleteCategory(Long id);
}
