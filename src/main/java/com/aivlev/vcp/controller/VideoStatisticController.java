package com.aivlev.vcp.controller;

import com.aivlev.vcp.model.Video;
import com.aivlev.vcp.service.VideoStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
