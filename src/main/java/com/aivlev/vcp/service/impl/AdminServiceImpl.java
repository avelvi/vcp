package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.exception.ModelNotFoundException;
import com.aivlev.vcp.model.User;
import com.aivlev.vcp.repository.storage.UserRepository;
import com.aivlev.vcp.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Created by aivlev on 4/29/16.
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User findUser(String id) {
        User user = userRepository.findOne(id);
        if(user != null){
            user.setPassword("");
            return user;
        } else {
            throw new ModelNotFoundException("User not found");
        }
    }
}
