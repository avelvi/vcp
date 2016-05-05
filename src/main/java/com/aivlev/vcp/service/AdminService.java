package com.aivlev.vcp.service;

import com.aivlev.vcp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by aivlev on 4/29/16.
 */
public interface AdminService {

    Page<User> findAllUsers(Pageable pageable);

    User findUser(String id);
}