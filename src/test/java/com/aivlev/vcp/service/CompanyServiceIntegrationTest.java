package com.aivlev.vcp.service;

import com.aivlev.vcp.config.TestConfig;
import com.aivlev.vcp.config.TestMongoConfig;
import com.aivlev.vcp.exception.DuplicateEntityException;
import com.aivlev.vcp.exception.ModelNotFoundException;
import com.aivlev.vcp.model.Company;
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
public class CompanyServiceIntegrationTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CompanyService companyService;

    private Pageable pageable;

    @Before
    public void setUp() throws Exception {
        pageable = new PageRequest(0, 5);
        Company company = new Company("Company", "Company address", "company@company.test", "000-000-00-00");
        mongoTemplate.insert(company);
    }

    @Test
    public void testFindAllCompanies() throws Exception {
        Page<Company> result = companyService.findAllCompanies(pageable);
        assertNotNull(result);
        assertEquals(5, result.getSize());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    public void testFindCompany() throws Exception {
        Company company = new Company("Company1", "Company address1", "company1@company.test", "000-000-00-00");
        companyService.createOrUpdate(null, company);
        Company result = companyService.findCompany(company.getId());
        assertNotNull(result);
    }

    @Test
    public void testFindCompanyWithModelNotFoundException() throws Exception {
        try{
            companyService.findCompany("1");
        } catch (ModelNotFoundException ex){
            String expected = "Company not found";
            assertEquals(expected, ex.getMessage());
        }

    }

    @Test
    public void testCreateOrUpdate() throws Exception {
        Company company = new Company("Company1", "Company address1", "company1@company.test", "000-000-00-00");
        companyService.createOrUpdate(null, company);
        company.setName("Updated Company Name");
        companyService.createOrUpdate(company.getId(), company);
        Company updatedCompany = companyService.findCompany(company.getId());
        assertEquals(company.getName(), updatedCompany.getName());
    }

    @Test
    public void testCreateOrUpdateWithModelNotFoundException() throws Exception {
        Company company = new Company("1", "Failed Company", "Company address1", "company1@company.test", "000-000-00-00");
        try {
            companyService.createOrUpdate(company.getId(), company);
        } catch (ModelNotFoundException ex){
            String expected = "Company not found";
            assertEquals(expected, ex.getMessage());
        }

    }

    @Test
    public void testCreateOrUpdateWithDuplicateEntityException() throws Exception {
        Company company = new Company("Company 1", "Company address1", "company1@company.test", "000-000-00-00");
        companyService.createOrUpdate(null, company);
        company.setName("Company");
        try{
            companyService.createOrUpdate(company.getId(), company);
        } catch (DuplicateEntityException ex){
            String expected = "Company with the same name is exists";
            assertEquals(expected, ex.getMessage());
        }


    }

    @Test
    public void testDeleteCompany() throws Exception {
        Company company = new Company("Company 1", "Company address1", "company1@company.test", "000-000-00-00");
        companyService.createOrUpdate(null, company);
        Page<Company> all = companyService.findAllCompanies(pageable);
        assertEquals(2, all.getTotalElements());
        companyService.deleteCompany(company.getId());
        all = companyService.findAllCompanies(pageable);
        assertEquals(1, all.getTotalElements());

    }

    @Test
    public void testDeleteCompanyWithModelNotFoundException() throws Exception {
        try{
            companyService.deleteCompany("1");
        } catch (ModelNotFoundException ex){
            String expected = "Company not found";
            assertEquals(expected, ex.getMessage());
        }

    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(Company.class);
    }
}