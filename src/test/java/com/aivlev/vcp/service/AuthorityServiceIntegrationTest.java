package com.aivlev.vcp.service;

import com.aivlev.vcp.config.TestConfig;
import com.aivlev.vcp.config.TestMongoConfig;
import com.aivlev.vcp.exception.ModelNotFoundException;
import com.aivlev.vcp.model.Authority;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by aivlev on 6/4/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestMongoConfig.class, TestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class AuthorityServiceIntegrationTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AuthorityService authorityService;

    @Before
    public void setUp() throws Exception {
        Authority userAuthority = new Authority("user");
        Authority adminAuthority = new Authority("admin");
        mongoTemplate.insert(userAuthority);
        mongoTemplate.insert(adminAuthority);
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(Authority.class);
    }

    @Test
    public void testFindByName() throws Exception {
        Authority authority = authorityService.findByName("user");
        assertNotNull(authority);
        assertEquals("user", authority.getName());
    }

    @Test
    public void testFindByNameWithModelNotFoundException() throws Exception {
        try{
            authorityService.findByName("some_user");
        } catch (ModelNotFoundException ex){
            String expected = "Authority not found";
            assertEquals(expected, ex.getMessage());
        }
    }

    @Test
    public void testFindAll() throws Exception {
        List<Authority> all = authorityService.findAll();
        assertNotNull(all);
        assertEquals(2, all.size());

    }
}