package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.model.Authority;
import com.aivlev.vcp.repository.storage.AuthorityRepository;
import com.aivlev.vcp.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;

/**
 * Created by aivlev on 5/9/16.
 */
@Service
public class AuthorityServiceImpl implements AuthorityService {

    @Autowired
    AuthorityRepository authorityRepository;

    @Override
    public HashSet<Authority> findByName(String name) {
        return authorityRepository.findByName(name);
    }

    @Override
    public Page<Authority> findAllAuthorities(Pageable pageable) {
        return authorityRepository.findAll(pageable);
    }
}
