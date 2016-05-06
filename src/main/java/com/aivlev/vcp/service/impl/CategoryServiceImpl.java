package com.aivlev.vcp.service.impl;

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
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category findCategory(String id) {
        return categoryRepository.findOne(id);
    }

    @Override
    public Category updateCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(String id) {
        categoryRepository.delete(id);
    }
}
