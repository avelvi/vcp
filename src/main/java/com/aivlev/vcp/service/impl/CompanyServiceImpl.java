package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.exception.DuplicateEntityException;
import com.aivlev.vcp.exception.ModelNotFoundException;
import com.aivlev.vcp.model.Company;
import com.aivlev.vcp.repository.storage.CompanyRepository;
import com.aivlev.vcp.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;

/**
 * Created by aivlev on 5/5/16.
 */
@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Override
    public Page<Company> findAllCompanies(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }

    @Override
    public Company findCompany(String id) {
        Company company = companyRepository.findOne(id);
        if(company != null) {
            return company;
        } else {
            throw new ModelNotFoundException("Company not found");
        }
    }

    @Override
    @Transactional
    public void createOrUpdate(String id, Company company) {
        if(id != null){
            Company companyFromDB = companyRepository.findOne(id);
            if(companyFromDB == null){
                throw new ModelNotFoundException("Company not found");
            } else {
                try {
                    companyRepository.save(company);
                } catch (Exception ex){
                    throw new DuplicateEntityException("Company with the same name is exists");
                }
            }
        } else {
            companyRepository.save(company);
        }
    }

    @Override
    @Transactional
    public void deleteCompany(String id) {
        Company company = companyRepository.findOne(id);
        if(company != null) {
            companyRepository.delete(id);
        } else {
            throw new ModelNotFoundException("Company not found");
        }
    }
}
