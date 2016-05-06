package com.aivlev.vcp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by aivlev on 5/5/16.
 */
@Document
public class Company {

    @Id
    private String id;
    @Indexed(unique = true, name = "company_name")
    private String name;
    private String address;
    private String email;
    private String phone;

    public Company() {
        super();
    }

    public Company(String name, String address, String email, String phone) {
        super();
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return String.format("Company [" +
                "id=%s, " +
                "name=%s, " +
                "address=%s]", id, name, address);
    }
}
