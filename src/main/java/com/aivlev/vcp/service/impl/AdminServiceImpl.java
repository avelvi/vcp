package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.dto.StatisticsDto;
import com.aivlev.vcp.model.User;
import com.aivlev.vcp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by aivlev on 4/29/16.
 */
@Service
@Transactional
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
        return userService.findOne(id);
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
