package com.aivlev.vcp.controller;

import com.aivlev.vcp.model.*;
import com.aivlev.vcp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public Object getAuthorities(@PageableDefault(size = 5)Pageable pageable){
        List<Authority> authorities = authorityService.findAllAuthorities(pageable);
        return new ResponseEntity<>(authorities, HttpStatus.OK);
    }

}
