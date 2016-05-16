package com.aivlev.vcp.controller;

import com.aivlev.vcp.dto.UserDto;
import com.aivlev.vcp.model.Authority;
import com.aivlev.vcp.model.ResponseHolder;
import com.aivlev.vcp.model.User;
import com.aivlev.vcp.model.Video;
import com.aivlev.vcp.service.CommonService;
import com.aivlev.vcp.service.UserService;
import com.aivlev.vcp.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by aivlev on 4/19/16.
 */

@RestController
public class CommonController {

    @Autowired
    private CommonService commonService;

    @Autowired
    private VideoService videoService;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void register(@RequestBody UserDto userDto){

        userService.registerUser(userDto);
    }
}
