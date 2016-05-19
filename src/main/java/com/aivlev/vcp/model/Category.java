package com.aivlev.vcp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by aivlev on 5/6/16.
 */
@Document
@org.springframework.data.elasticsearch.annotations.Document(indexName = "category")
public class Category {

    @Id
    private String id;
    @Indexed(unique = true, name = "category_name")
    private String name;

    public Category() {
        super();
    }

    public Category(String name) {
        super();
        this.name = name;
    }

    public Category(String id, String name) {
        super();
        this.id = id;
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
