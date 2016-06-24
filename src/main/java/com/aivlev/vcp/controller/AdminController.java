package com.aivlev.vcp.controller;

import com.aivlev.vcp.dto.StatisticsDto;
import com.aivlev.vcp.model.Authority;
import com.aivlev.vcp.service.AdminService;
import com.aivlev.vcp.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by aivlev on 4/29/16.
 */
@RestController
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    AuthorityService authorityService;

    @Autowired
    AdminService adminService;


    @PreAuthorize("hasAuthority('admin')")
    @RequestMapping(value = "/authorities", method = RequestMethod.GET)
    public Object getAuthoritiesNames(){
        List<Authority> result = authorityService.findAll();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('admin')")
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public Object getStatistics(){
        StatisticsDto statisticsDto = adminService.getStatistics();
        return new ResponseEntity<>(statisticsDto, HttpStatus.OK);
    }

}
