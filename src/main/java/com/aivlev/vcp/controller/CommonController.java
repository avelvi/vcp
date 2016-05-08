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

    @RequestMapping(value = "/videos", method = RequestMethod.GET)
    public Object getAllVideos(@PageableDefault(size = 9)Pageable pageable){
        ResponseHolder<Page<Video>> result = commonService.findAllVideos(pageable);
        return new ResponseEntity<>(result.getResponse(), HttpStatus.OK);
    }

    @RequestMapping(value = "/video/{id}", method = RequestMethod.GET)
    public Object getVideo(@PathVariable(value = "id") String id) {
        ResponseHolder<Video> result = videoService.findOne(id);
        return new ResponseEntity<>(result.getResponse(), HttpStatus.OK);
    }

    @RequestMapping(value = "/videos/search", method = RequestMethod.GET)
    public Object findVideos(@RequestParam("query") String query, @PageableDefault(size = 9) Pageable pageable) {
        ResponseHolder<Page<Video>> result = commonService.findAllVideosBySearchQuery(query, pageable);
        return new ResponseEntity<>(result.getResponse(), HttpStatus.OK);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void register(@RequestBody UserDto userDto){

        User user = userService.findByLogin(userDto.getLogin());

        if(null == user){
            User newUser = new User();
            newUser.setLogin(userDto.getLogin());
            newUser.setEmail(userDto.getEmail());
            newUser.setPassword(userDto.getPassword());
            newUser.setAuthorities(new HashSet<>(Arrays.asList(new Authority("user"))));
            userService.save(null, newUser);
        }
    }
}
