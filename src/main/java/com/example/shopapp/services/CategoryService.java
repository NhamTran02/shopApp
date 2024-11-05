package com.example.shopapp.services;

import com.example.shopapp.DTO.CategoryDTO;
import com.example.shopapp.model.Category;
import com.example.shopapp.repositories.CategoryRepository;
import com.example.shopapp.services.impl.CategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService implements CategoryServiceImpl {
    @Autowired
    private CategoryRepository categoryRepository;

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public Category createCategory(CategoryDTO categoryDTO) {
        Category newCategory = Category.builder()
                .name(categoryDTO.getName())
                .build();
        return categoryRepository.save(newCategory);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Category not found"));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public Category updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        Category existingCategory = getCategoryById(categoryId);
        existingCategory.setName(categoryDTO.getName());
        categoryRepository.save(existingCategory);
        return existingCategory;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public void deleteCategory(Long id) {
        //Xoá cứng
        categoryRepository.deleteById(id);
    }
}
