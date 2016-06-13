package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.config.TestMockConfig;
import com.aivlev.vcp.exception.ModelNotFoundException;
import com.aivlev.vcp.model.Authority;
import com.aivlev.vcp.repository.storage.AuthorityRepository;
import com.aivlev.vcp.service.AuthorityService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by aivlev on 6/9/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestMockConfig.class})
public class AuthorityServiceImplTest {

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Mock
    private List<Authority> authorityList;

    @Mock
    private Authority authority;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindByName() {
        when(authorityRepository.findByName(anyString())).thenReturn(authority);
        authorityService.findByName(anyString());
        verify(authorityRepository).findByName(anyString());
    }

    @Test(expected = ModelNotFoundException.class)
    public void testFindByNameWithModelNotFoundException() {
        when(authorityRepository.findByName(anyString())).thenReturn(null);
        authorityService.findByName(anyString());
    }

    @Test
    public void testFindAllAuthorities() {
        when(authorityRepository.findAll()).thenReturn(authorityList);
        authorityService.findAll();
        verify(authorityRepository).findAll();
    }

}
