package com.aivlev.vcp.controller;

import com.aivlev.vcp.model.Video;
import com.aivlev.vcp.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by aivlev on 5/17/16.
 */
@RestController
@RequestMapping(value = "/search")
public class SearchController {

    @Autowired
    VideoService videoService;

    @RequestMapping(method = RequestMethod.GET)
    public Object findVideos(@RequestParam("query") String query, @PageableDefault(size = 12) Pageable pageable) {
        Page<Video> result = videoService.findAllVideosBySearchQuery(query, pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
