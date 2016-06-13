package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.config.TestMockConfig;
import com.aivlev.vcp.exception.ModelNotFoundException;
import com.aivlev.vcp.model.User;
import com.aivlev.vcp.repository.storage.UserRepository;
import com.aivlev.vcp.service.AdminService;
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

import static org.mockito.Mockito.*;

/**
 * Created by aivlev on 6/9/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestMockConfig.class})
public class AdminServiceImplTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private Pageable pageable;

    @Mock
    private Page<User> userPage;

    @Mock
    private User user;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindAllUsers(){
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        adminService.findAllUsers(pageable);
        verify(userRepository).findAll(pageable);
    }

    @Test
    public void testFindUser() {
        String id = "someId";
        when(userRepository.findOne(id)).thenReturn(user);
        adminService.findUser(id);
        verify(userRepository, times(1)).findOne(id);
    }

    @Test(expected = ModelNotFoundException.class)
    public void testFindUserWithModelNotFoundException() {
        when(userRepository.findOne(anyString())).thenReturn(null);
        adminService.findUser(anyString());
    }
}
