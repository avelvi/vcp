package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.exception.DuplicateEntityException;
import com.aivlev.vcp.exception.ModelNotFoundException;
import com.aivlev.vcp.model.Category;
import com.aivlev.vcp.repository.storage.CategoryRepository;
import com.aivlev.vcp.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by aivlev on 5/6/16.
 */
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public Category findOne(String id) {
        Category category = categoryRepository.findOne(id);
        if(category != null){
            return category;
        } else {
            LOGGER.error("Category with id = " + id + " not found");
            throw new ModelNotFoundException("Category not found");
        }
    }

    @Override
    public void createOrUpdate(String id, Category category) {
        if(id != null){
            Category categoryFromDB = categoryRepository.findOne(id);
            if(categoryFromDB == null){
                LOGGER.error("Category with id = " + id + " not found");
                throw new ModelNotFoundException("Category not found");
            }
        }
        try {
            categoryRepository.save(category);
        } catch (Exception ex) {
            LOGGER.error("Category with the name = " + category.getName() + " is exists", ex.getMessage());
            throw new DuplicateEntityException("Category with the same name is exists");
        }
    }

    @Override
    public void deleteCategory(String id) {
        Category categoryFromDB = categoryRepository.findOne(id);
        if(categoryFromDB != null){
            categoryRepository.delete(id);
        } else {
            LOGGER.error("Error has occurred while deleting category with id = " + id + ". Category not found");
            throw new ModelNotFoundException("Category not found");
        }
    }

    @Override
    public long count() {
        return categoryRepository.count();
    }
}
