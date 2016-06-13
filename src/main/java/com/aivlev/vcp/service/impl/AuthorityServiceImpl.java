package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.exception.ModelNotFoundException;
import com.aivlev.vcp.model.Authority;
import com.aivlev.vcp.repository.storage.AuthorityRepository;
import com.aivlev.vcp.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by aivlev on 5/9/16.
 */
@Service
public class AuthorityServiceImpl implements AuthorityService {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Override
    public Authority findByName(String name) {
        Authority authority = authorityRepository.findByName(name);
        if(authority == null){
            throw new ModelNotFoundException("Authority not found");
        } else return authority;
    }

    @Override
    public List<Authority> findAll() {
        return authorityRepository.findAll();
    }
}
