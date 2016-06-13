package com.aivlev.vcp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by aivlev on 4/27/16.
 */
@Document
public class Authority {

    @Id
    private String id;
    @Indexed(unique = true, name = "authority_name")
    private String name;

    public Authority() {
        super();
    }

    public Authority(String name) {
        this();
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
