package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.exception.DuplicateEntityException;
import com.aivlev.vcp.exception.ModelNotFoundException;
import com.aivlev.vcp.model.Company;
import com.aivlev.vcp.repository.storage.CompanyRepository;
import com.aivlev.vcp.service.CompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by aivlev on 5/5/16.
 */
@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyServiceImpl.class);

    @Autowired
    private CompanyRepository companyRepository;

    @Override
    public Page<Company> findAll(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }

    @Override
    public Company findOne(String id) {
        Company company = companyRepository.findOne(id);
        if(company != null) {
            return company;
        } else {
            LOGGER.error("Company with id = " + id + " not found");
            throw new ModelNotFoundException("Company not found");
        }
    }

    @Override
    public void createOrUpdate(String id, Company company) {
        if(id != null){
            Company companyFromDB = companyRepository.findOne(id);
            if(companyFromDB == null){
                LOGGER.error("Company with id = " + id + " not found");
                throw new ModelNotFoundException("Company not found");
            }
        }
        try {
            companyRepository.save(company);
        } catch (Exception ex) {
            LOGGER.error("Company with the name = " + company.getName() + " is exists", ex.getMessage());
            throw new DuplicateEntityException("Company with the same name is exists");
        }
    }

    @Override
    public void deleteCompany(String id) {
        Company company = companyRepository.findOne(id);
        if(company != null) {
            companyRepository.delete(id);
        } else {
            LOGGER.error("Error has occurred while deleting company with id = " + id + ". Company not found");
            throw new ModelNotFoundException("Company not found");
        }
    }

    @Override
    public long count() {
        return companyRepository.count();
    }
}
