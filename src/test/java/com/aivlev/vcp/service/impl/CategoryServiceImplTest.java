package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.config.TestMockConfig;
import com.aivlev.vcp.exception.ModelNotFoundException;
import com.aivlev.vcp.model.Category;
import com.aivlev.vcp.repository.storage.CategoryRepository;
import com.aivlev.vcp.service.CategoryService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by aivlev on 6/9/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestMockConfig.class})
public class CategoryServiceImplTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Mock
    private Pageable pageable;

    @Mock
    private Page<Category> categoryPage;

    @Mock
    private Category category;

    @Before
    public void setUp() throws Exception {
        reset(categoryRepository);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindAllCategories() throws Exception {
        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        categoryService.findAll(pageable);
        verify(categoryRepository).findAll(pageable);

    }

    @Test
    public void testFindCategory() throws Exception {
        String id = "someId";
        when(categoryRepository.findOne(id)).thenReturn(category);
        categoryService.findOne(id);
        verify(categoryRepository).findOne(id);
    }

    @Test(expected = ModelNotFoundException.class)
    public void testFindCategoryWithException() throws Exception {
        when(categoryRepository.findOne(anyString())).thenReturn(null);
        categoryService.findOne(anyString());
    }

    @Test
    public void testCreateOrUpdate() throws Exception {
        when(categoryRepository.findOne(anyString())).thenReturn(category);
        categoryService.createOrUpdate(anyString(), category);
        verify(categoryRepository).save(category);

    }

    @Test
    public void testCreateOrUpdate2() throws Exception {
        categoryService.createOrUpdate(null, category);
        verify(categoryRepository).save(category);

    }

    @Test(expected = ModelNotFoundException.class)
    public void testCreateOrUpdateWithModelNotFoundException() throws Exception {
        when(categoryRepository.findOne(anyString())).thenReturn(null);
        categoryService.createOrUpdate(anyString(), category);
        verify(categoryRepository, times(0)).save(category);

    }

    @Test(expected = Exception.class)
    public void testCreateOrUpdateWithException() throws Exception {
        when(categoryRepository.findOne(anyString())).thenReturn(category);
        when(categoryRepository.save(category)).thenThrow(Exception.class);
        categoryService.createOrUpdate(anyString(), category);
        verify(categoryRepository, times(0)).save(category);
    }

    @Test
    public void testDeleteCategory() throws Exception {
        when(categoryRepository.findOne(anyString())).thenReturn(category);
        categoryService.deleteCategory(anyString());
        verify(categoryRepository).delete(anyString());
    }

    @Test(expected = ModelNotFoundException.class)
    public void testDeleteCategoryWithException() throws Exception {
        when(categoryRepository.findOne(anyString())).thenReturn(null);
        categoryService.deleteCategory(anyString());
        verify(categoryRepository, times(0)).delete(anyString());
    }

    @Test
    public void testCount() throws Exception {
        when(categoryRepository.count()).thenReturn(1l);
        categoryService.count();
        verify(categoryRepository, times(1)).count();
    }
}
