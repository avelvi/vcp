package com.aivlev.vcp.controller;

import com.aivlev.vcp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by aivlev on 5/1/16.
 */
@RestController
public class UserController {

//    @RequestMapping(value = "/users/id", method = RequestMethod.PUT)
//    public Object getAllUsers(@RequestBody){
//        Page<User> result = adminService.findAllUsers(pageable);
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }
}
