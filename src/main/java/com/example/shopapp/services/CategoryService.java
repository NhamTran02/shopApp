package com.example.shopapp.services;

import com.example.shopapp.DTO.CategoryDTO;
import com.example.shopapp.model.Category;
import com.example.shopapp.repository.CategoryRepository;
import com.example.shopapp.services.impl.CategoryServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService implements CategoryServiceImpl {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private RedisTemplate redisTemplate;

    private Gson gson=new Gson();

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
        String dataRedis = (String) redisTemplate.opsForValue().get("category");
        List<Category> categories=new ArrayList<>();
        if(dataRedis==null){
            System.out.println("Chưa có data");
            categories = categoryRepository.findAll();
            String dataJson = gson.toJson(categories) ;
            redisTemplate.opsForValue().set("category", dataJson);
        }else {
            Type type = new TypeToken<List<Category>>(){}.getType();
            categories=gson.fromJson(dataRedis,type);
            System.out.println("Có data");
        }

        return categories;
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
