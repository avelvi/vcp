package com.aivlev.vcp.repository.storage;

import com.aivlev.vcp.model.Company;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by aivlev on 5/5/16.
 */
public interface CompanyRepository extends PagingAndSortingRepository<Company, String> {
}
