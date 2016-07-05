package com.aivlev.vcp.service;

import com.aivlev.vcp.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * Created by aivlev on 5/6/16.
 */
public interface CategoryService {

    Page<Category> findAll(Pageable pageable);

    Category findOne(String id);

    void createOrUpdate(String id, Category category);

    void deleteCategory(String id);

    long count();
}
