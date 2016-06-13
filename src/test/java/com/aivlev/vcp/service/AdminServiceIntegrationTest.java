package com.aivlev.vcp.service;

import com.aivlev.vcp.config.TestConfig;
import com.aivlev.vcp.config.TestMongoConfig;
import com.aivlev.vcp.exception.ModelNotFoundException;
import com.aivlev.vcp.model.Authority;
import com.aivlev.vcp.model.Company;
import com.aivlev.vcp.model.User;
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

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by aivlev on 6/4/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestMongoConfig.class, TestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class AdminServiceIntegrationTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AdminService adminService;

    private Pageable pageable;

    private User user1;

    @Before
    public void setUp() throws Exception {
        pageable = new PageRequest(0, 5);
        Company company = new Company("Company 1", "Company address", "company@company.test", "000-000-00-00");
        Company company2 = new Company("Company 2", "Company address", "company@company.test", "000-000-00-00");
        Authority userAuthority = new Authority("user");
        Authority adminAuthority = new Authority("admin");
        mongoTemplate.insert(company);
        mongoTemplate.insert(company2);
        mongoTemplate.insert(userAuthority);
        mongoTemplate.insert(adminAuthority);
        user1 = new User("User 1", "User Surname 1", "login1", "test1@testvcp.ua", "12345",
                null, company, true);
        User user2 = new User("User 2", "User Surname 2", "login2", "test2@testvcp.ua", "12345",
                null, company, true);
        user1.setAuthorities(Arrays.asList(userAuthority));
        user2.setAuthorities(Arrays.asList(userAuthority, adminAuthority));
        mongoTemplate.insert(user1);
        mongoTemplate.insert(user2);
    }

    @Test
    public void testFindAllUsers() throws Exception {
        Page<User> result = adminService.findAllUsers(pageable);
        assertNotNull(result);
        assertEquals(5, result.getSize());
        assertEquals(2, result.getTotalElements());
    }

    @Test
    public void testFindUser() throws Exception {
        User result = adminService.findUser(user1.getId());
        assertNotNull(result);
        assertEquals(user1.getName(), result.getName());
    }

    @Test
    public void testFindUserWithModelNotFoundException() throws Exception {
        try{
            adminService.findUser("1");
        } catch (ModelNotFoundException ex){
            String expected = "User not found";
            assertEquals(expected, ex.getMessage());
        }
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(Company.class);
        mongoTemplate.dropCollection(User.class);
        mongoTemplate.dropCollection(Authority.class);
    }
}