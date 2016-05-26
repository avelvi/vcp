package com.aivlev.vcp.service;

import com.aivlev.vcp.model.Authority;

import java.util.List;

/**
 * Created by aivlev on 5/9/16.
 */
public interface AuthorityService {
    Authority findByName(String name);

    List<Authority> findAll();
}
