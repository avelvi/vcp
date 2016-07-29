package com.aivlev.vcp.controller;

import com.aivlev.vcp.service.VideoStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by aivlev on 6/24/16.
 */
@RestController
@RequestMapping(value = "/statistic")
public class VideoStatisticController {

    @Autowired
    private VideoStatisticService videoStatisticService;

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public void updateVideo(@PathVariable(value = "id") String id,
                            HttpServletRequest request){
        videoStatisticService.updateVideoViews(id, request.getRemoteAddr());
    }
}
