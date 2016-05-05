package com.aivlev.vcp.controller;

import com.aivlev.vcp.model.ResponseHolder;
import com.aivlev.vcp.model.User;
import com.aivlev.vcp.model.Video;
import com.aivlev.vcp.service.AdminService;
import com.aivlev.vcp.service.UserService;
import com.aivlev.vcp.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by aivlev on 4/29/16.
 */
@RestController
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    AdminService adminService;

    @Autowired
    VideoService videoService;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public Object getAllUsers(@PageableDefault(size = 10)Pageable pageable){
        Page<User> result = adminService.findAllUsers(pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public Object getUser(@PathVariable(value = "id") String id){
        User user = adminService.findUser(id);
        if(user == null){
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/videos", method = RequestMethod.GET)
    public Object getUser(@RequestParam("userId") String userId, @PageableDefault(size = 12)Pageable pageable){
        ResponseHolder<Page<Video>> result = videoService.findAllVideosByOwnerId(userId, pageable);
        return new ResponseEntity<>(result.getResponse(), HttpStatus.OK);
    }

}
