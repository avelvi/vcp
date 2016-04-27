package com.aivlev.vcp.controller;

import com.aivlev.vcp.model.ResponseHolder;
import com.aivlev.vcp.model.Video;
import com.aivlev.vcp.service.CommonService;
import com.aivlev.vcp.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by aivlev on 4/19/16.
 */

@RestController
public class CommonController {

    @Autowired
    private CommonService commonService;

    @Autowired
    private VideoService videoService;

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
}
