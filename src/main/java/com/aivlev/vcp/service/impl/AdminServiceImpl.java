package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.dto.StatisticsDto;
import com.aivlev.vcp.exception.ModelNotFoundException;
import com.aivlev.vcp.model.User;
import com.aivlev.vcp.repository.storage.UserRepository;
import com.aivlev.vcp.service.*;
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
    private UserService userService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CategoryService categoryService;


    @Override
    public Page<User> findAllUsers(Pageable pageable) {
        return userService.findAllUsers(pageable);
    }

    @Override
    public User findUser(String id) {
        User user = userService.findOne(id);
        if(user != null){
            user.setPassword("");
            return user;
        } else {
            throw new ModelNotFoundException("User not found");
        }
    }

    @Override
    public StatisticsDto getStatistics() {

        StatisticsDto statisticsDto = new StatisticsDto();
        statisticsDto.setUsersCount(userService.count());
        statisticsDto.setVideosCount(videoService.count());
        statisticsDto.setCompaniesCount(companyService.count());
        statisticsDto.setCategoriesCount(categoryService.count());
        return statisticsDto;
    }


}
