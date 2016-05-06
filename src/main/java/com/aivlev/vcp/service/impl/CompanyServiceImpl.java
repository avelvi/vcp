package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.model.Company;
import com.aivlev.vcp.repository.storage.CompanyRepository;
import com.aivlev.vcp.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Created by aivlev on 5/5/16.
 */
@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    CompanyRepository companyRepository;

    @Override
    public Page<Company> findAllCompanies(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }

    @Override
    public Company saveCompany(Company company) {
        return companyRepository.save(company);
    }

    @Override
    public Company findCompany(String id) {
        return companyRepository.findOne(id);
    }

    @Override
    public Company updateCompany(Company company) {
        return companyRepository.save(company);
    }

    @Override
    public void deleteCompany(String id) {
        companyRepository.delete(id);
    }
}
