package com.aivlev.vcp.controller;

import com.aivlev.vcp.model.ResponseHolder;
import com.aivlev.vcp.model.Video;
import com.aivlev.vcp.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by aivlev on 4/19/16.
 */

@Controller
public class VideoController {

    @Autowired
    VideoService videoService;

    @RequestMapping(value = "/videos", method = RequestMethod.GET)
    public Object getAllVideos(@PageableDefault(size = 10)Pageable pageable){
        ResponseHolder<Page<Video>> result = videoService.findAllVideos(pageable);
        return new ResponseEntity<>(result.getResponse(), HttpStatus.OK);
    }
}
