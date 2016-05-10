package com.aivlev.vcp.service;

import com.aivlev.vcp.model.Authority;

import java.util.HashSet;

/**
 * Created by aivlev on 5/9/16.
 */
public interface AuthorityService {
    HashSet<Authority> findByName(String name);
}
