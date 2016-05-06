package com.aivlev.vcp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by aivlev on 4/19/16.
 */

@Document
public class User implements Serializable{

    private static final long serialVersionUID = -4891639544969424612L;

    @Id
    private String id;
    private String name;
    private String surname;
    @Indexed(unique = true, name = "user_login")
    private String login;
    @Indexed(unique = true, name = "user_email")
    private String email;
    private String password;
    private Set<Authority> authorities;
    private String avatar;
    @DBRef(lazy = true)
    private Company company;

    public User(){
        super();
    }

    public User(String name, String surname, String login, String email, String password,
//                Set<Authority> authorities,
                String avatar, Company company) {
        super();
        this.name = name;
        this.surname = surname;
        this.login = login;
        this.email = email;
        this.password = password;
//        this.authorities = authorities;
        this.avatar = avatar;
        this.company = company;
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @Override
    public String toString() {
        return String.format("User [" +
                "id=%s, " +
                "name=%s, " +
                "surname=%s, " +
                "login=%s " +
                "authorities=%s]", id, name, surname, login, authorities, avatar);
    }

}
