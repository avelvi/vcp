package com.aivlev.vcp.controller;

import com.aivlev.vcp.model.Authority;
import com.aivlev.vcp.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


    @RequestMapping(value = "/authorities", method = RequestMethod.GET)
    public Object getAuthoritiesNames(){
        List<Authority> result = authorityService.findAll();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
