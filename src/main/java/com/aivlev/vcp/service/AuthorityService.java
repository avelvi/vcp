package com.aivlev.vcp.service;

import com.aivlev.vcp.model.Authority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;

/**
 * Created by aivlev on 5/9/16.
 */
public interface AuthorityService {
    HashSet<Authority> findByName(String name);

    Page<Authority> findAllAuthorities(Pageable pageable);
}
