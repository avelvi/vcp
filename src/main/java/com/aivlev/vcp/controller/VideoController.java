package com.aivlev.vcp.controller;

import com.aivlev.vcp.model.Video;
import com.aivlev.vcp.service.UserService;
import com.aivlev.vcp.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by aivlev on 5/13/16.
 */
@RestController
@RequestMapping(value = "/videos")
public class VideoController extends GenericController{

    @Autowired
    VideoService videoService;

    @Autowired
    UserService userService;


    @RequestMapping(method = RequestMethod.GET)
    public Object getAllVideos(@PageableDefault(size = 12)Pageable pageable){

        Page<Video> result = videoService.findAll(pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object getVideo(@PathVariable(value = "id") String id) {
        Video result = videoService.findOne(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteVideo(
            @PathVariable(value = "id") String id,
            @AuthenticationPrincipal UserDetails userDetails){
        videoService.deleteVideo(isAdmin(userDetails), userDetails.getUsername(), id);
    }

    @PreAuthorize("hasAuthority('user')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public void updateVideo(@PathVariable(value = "id") String id,
                              @RequestBody Video video,
                              @AuthenticationPrincipal UserDetails userDetails){
        videoService.updateVideo(isAdmin(userDetails), userDetails.getUsername(), id, video);
    }
}
