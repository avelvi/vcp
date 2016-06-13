package com.aivlev.vcp.service;

import com.aivlev.vcp.config.TestConfig;
import com.aivlev.vcp.config.TestMongoConfig;
import com.aivlev.vcp.exception.DuplicateEntityException;
import com.aivlev.vcp.exception.ModelNotFoundException;
import com.aivlev.vcp.model.Category;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by aivlev on 6/2/16.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestMongoConfig.class, TestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class CategoryServiceIntegrationTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CategoryService categoryService;



    private Pageable pageable;

    @Before
    public void setUp() throws Exception {
        pageable = new PageRequest(0, 5);
        Category category = new Category("Category");
        mongoTemplate.insert(category);
    }

    @Test
    public void testFindAllCategories() throws Exception {
        Page<Category> result = categoryService.findAllCategories(pageable);
        assertNotNull(result);
        assertEquals(5, result.getSize());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    public void testFindCategory() throws Exception {
        Category category = new Category("Category 1");
        categoryService.createOrUpdate(null, category);
        Category result = categoryService.findCategory(category.getId());
        assertNotNull(result);
    }

    @Test
    public void testFindCategoryWithModelNotFoundException() throws Exception {
        try{
            categoryService.findCategory("1");
        } catch (ModelNotFoundException ex){
            String expected = "Category not found";
            assertEquals(expected, ex.getMessage());
        }

    }

    @Test
    public void testCreateOrUpdate() throws Exception {
        Category category = new Category("Category 1");
        categoryService.createOrUpdate(null, category);
        category.setName("Updated Category Name");
        categoryService.createOrUpdate(category.getId(), category);
        Category updatedCategory = categoryService.findCategory(category.getId());
        assertEquals(category.getName(), updatedCategory.getName());
    }

    @Test
    public void testCreateOrUpdateWithModelNotFoundException() throws Exception {
        Category category = new Category("1", "Failed Category");
        try {
            categoryService.createOrUpdate(category.getId(), category);
        } catch (ModelNotFoundException ex){
            String expected = "Category not found";
            assertEquals(expected, ex.getMessage());
        }

    }

    @Test
    public void testCreateOrUpdateWithDuplicateEntityException() throws Exception {
        Category category = new Category("Category 1");
        categoryService.createOrUpdate(null, category);
        category.setName("Category");
        try{
            categoryService.createOrUpdate(category.getId(), category);
        } catch (DuplicateEntityException ex){
            String expected = "Category with the same name is exists";
            assertEquals(expected, ex.getMessage());
        }


    }

    @Test
    public void testDeleteCategory() throws Exception {
        Category category = new Category("Category 1");
        categoryService.createOrUpdate(null, category);
        Page<Category> all = categoryService.findAllCategories(pageable);
        assertEquals(2, all.getTotalElements());
        categoryService.deleteCategory(category.getId());
        all = categoryService.findAllCategories(pageable);
        assertEquals(1, all.getTotalElements());

    }

    @Test
    public void testDeleteCategoryWithModelNotFoundException() throws Exception {
        try{
            categoryService.deleteCategory("1");
        } catch (ModelNotFoundException ex){
            String expected = "Category not found";
            assertEquals(expected, ex.getMessage());
        }

    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(Category.class);
    }
}