package com.aivlev.vcp.repository.storage;

import com.aivlev.vcp.model.Authority;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.HashSet;

/**
 * Created by aivlev on 4/27/16.
 */
public interface AuthorityRepository extends PagingAndSortingRepository<Authority, String> {

    HashSet<Authority> findByName(String name);
}
