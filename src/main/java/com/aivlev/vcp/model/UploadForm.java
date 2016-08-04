package com.aivlev.vcp.model;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by aivlev on 4/26/16.
 */
public class UploadForm {
    private String title;
    private String description;
    private MultipartFile file;

    public UploadForm() {
        super();
    }
    public UploadForm(MultipartFile file) {
        super();
        this.file = file;
    }
    public UploadForm(String title, String description, MultipartFile file) {
        super();
        this.title = title;
        this.description = description;
        this.file = file;
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
    public MultipartFile getFile() {
        return file;
    }
    public void setFile(MultipartFile file) {
        this.file = file;
    }

}
