package com.aivlev.vcp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by aivlev on 6/24/16.
 */
@Document
public class VideoStatistic {

    @Id
    private String id;
    private String videoId;
    private String ipAddress;


    public VideoStatistic() {
        super();
    }

    public VideoStatistic(String videoId, String ipAddress) {
        this();
        this.videoId = videoId;
        this.ipAddress = ipAddress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
