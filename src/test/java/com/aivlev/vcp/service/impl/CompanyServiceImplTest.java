package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.config.TestMockConfig;
import com.aivlev.vcp.exception.ModelNotFoundException;
import com.aivlev.vcp.model.Company;
import com.aivlev.vcp.repository.storage.CompanyRepository;
import com.aivlev.vcp.service.CompanyService;
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

import static org.mockito.Mockito.*;

/**
 * Created by aivlev on 5/27/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestMockConfig.class})
public class CompanyServiceImplTest {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyRepository companyRepository;

    @Mock
    private Pageable pageable;

    @Mock
    private Page<Company> companyPage;

    @Mock
    private Company company;

    @Before
    public void setUp() throws Exception {
        reset(companyRepository);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindAllCompanies() throws Exception {
        when(companyRepository.findAll(pageable)).thenReturn(companyPage);
        companyService.findAll(pageable);
        verify(companyRepository).findAll(pageable);

    }

    @Test
    public void testFindCompany() throws Exception {
        String id = "someId";
        when(companyRepository.findOne(id)).thenReturn(company);
        companyService.findOne(id);
        verify(companyRepository).findOne(id);
    }

    @Test(expected = ModelNotFoundException.class)
    public void testFindCompanyWithException() throws Exception {
        when(companyRepository.findOne(anyString())).thenReturn(null);
        companyService.findOne(anyString());
    }

    @Test
    public void testCreateOrUpdate() throws Exception {
        when(companyRepository.findOne(anyString())).thenReturn(company);
        companyService.createOrUpdate(anyString(), company);
        verify(companyRepository).save(company);

    }

    @Test
    public void testCreateOrUpdate2() throws Exception {
        companyService.createOrUpdate(null, company);
        verify(companyRepository).save(company);

    }

    @Test(expected = ModelNotFoundException.class)
    public void testCreateOrUpdateWithModelNotFoundException() throws Exception {
        when(companyRepository.findOne(anyString())).thenReturn(null);
        companyService.createOrUpdate(anyString(), company);
        verify(companyRepository, times(0)).save(company);

    }

    @Test(expected = Exception.class)
    public void testCreateOrUpdateWithException() throws Exception {
        when(companyRepository.findOne(anyString())).thenReturn(company);
        when(companyRepository.save(company)).thenThrow(Exception.class);
        companyService.createOrUpdate(anyString(), company);
        verify(companyRepository, times(0)).save(company);

    }

    @Test
    public void testDeleteCompany() throws Exception {
        when(companyRepository.findOne(anyString())).thenReturn(company);
        companyService.deleteCompany(anyString());
        verify(companyRepository).delete(anyString());
    }

    @Test(expected = ModelNotFoundException.class)
    public void testDeleteCompanyWithException() throws Exception {
        when(companyRepository.findOne(anyString())).thenReturn(null);
        companyService.deleteCompany(anyString());
        verify(companyRepository, times(0)).delete(anyString());
    }

    @Test
    public void testCount() throws Exception {
        when(companyRepository.count()).thenReturn(1l);
        companyService.count();
        verify(companyRepository).count();
    }
}