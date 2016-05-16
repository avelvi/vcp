package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.exception.DuplicateEntityException;
import com.aivlev.vcp.exception.ModelNotFoundException;
import com.aivlev.vcp.model.Category;
import com.aivlev.vcp.repository.storage.CategoryRepository;
import com.aivlev.vcp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


/**
 * Created by aivlev on 5/6/16.
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public Page<Category> findAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public Category findCategory(String id) {
        Category category = categoryRepository.findOne(id);
        if(null != category){
            return category;
        } else {
            throw new ModelNotFoundException("Category not found");
        }
    }

    @Override
    public void createOrUpdate(String id, Category category) {
        if(null != id){
            Category categoryFromDB = categoryRepository.findOne(id);
            if(categoryFromDB == null){
                throw new ModelNotFoundException("Category not found");
            }
        }
        try {
            categoryRepository.save(category);
        } catch (Exception ex){
            throw new DuplicateEntityException("Category is exists");
        }

    }

    @Override
    public void deleteCategory(String id) {
        Category categoryFromDB = categoryRepository.findOne(id);
        if(null != categoryFromDB){
            categoryRepository.delete(id);
        } else {
            throw new ModelNotFoundException("Category not found");
        }
    }
}
