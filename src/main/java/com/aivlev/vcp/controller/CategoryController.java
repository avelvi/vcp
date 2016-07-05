package com.aivlev.vcp.controller;

import com.aivlev.vcp.model.Category;
import com.aivlev.vcp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Created by aivlev on 5/16/16.
 */
@RestController
@RequestMapping(value = "/categories")
public class CategoryController {

    @Autowired
    CategoryService categoryService;


    @RequestMapping(method = RequestMethod.GET)
    public Object getCategories(@PageableDefault(size = 10)Pageable pageable){
        Page<Category> result = categoryService.findAll(pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('admin')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object getCategory(@PathVariable(value = "id") String id){
        Category category = categoryService.findOne(id);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('admin')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public void updateCategory(@PathVariable(value = "id") String id, @RequestBody Category category){
        categoryService.createOrUpdate(id, category);
    }

    @PreAuthorize("hasAuthority('admin')")
    @RequestMapping(method = RequestMethod.POST)
    public void createCategory(@RequestBody Category category){
        categoryService.createOrUpdate(null, category);
    }

    @PreAuthorize("hasAuthority('admin')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteCategory(@PathVariable(value = "id") String id){
        categoryService.deleteCategory(id);
    }
}
