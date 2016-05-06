package com.aivlev.vcp.service;

import com.aivlev.vcp.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by aivlev on 5/5/16.
 */
public interface CompanyService {
    Page<Company> findAllCompanies(Pageable pageable);

    Company saveCompany(Company company);

    Company findCompany(String id);

    Company updateCompany(Company company);

    void deleteCompany(String id);
}
