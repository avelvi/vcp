package com.aivlev.vcp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * Created by aivlev on 4/19/16.
 */

@Document
@org.springframework.data.elasticsearch.annotations.Document(indexName = "video")
public class Video {

    @Id
    private String id;
    @Field("title")
    private String title;
    private String description;
    @Field(value = "created_date")
    private String createdDate;
    @Field("video_url")
    private String videoUrl;
    private List<String> thumbnails;
    @DBRef(lazy = true)
    private User owner;

    public Video() {
        super();
    }

    public Video(String title, String description, String createdDate, String videoUrl, List<String> thumbnails, User owner) {
        super();
        this.title = title;
        this.description = description;
        this.createdDate = createdDate;
        this.videoUrl = videoUrl;
        this.thumbnails = thumbnails;
        this.owner = owner;
    }

    public Video(String createdDate, String videoUrl, List<String> thumbnails) {
        this(null, null, createdDate, videoUrl, thumbnails, null);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public List<String> getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(List<String> thumbnails) {
        this.thumbnails = thumbnails;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return String.format("Video [id=%s, title=%s, videoUrl=%s, thumbnails=%s, owner=%s]", id, title, videoUrl, thumbnails, owner);
    }
}
