package com.aivlev.vcp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by aivlev on 4/19/16.
 */

@Document
public class User {

    @Id
    private String id;
    private String name;
    private String surname;
    @Indexed(unique = true, name = "user_login")
    private String login;
    @Indexed(unique = true, name = "user_email")
    private String email;
    private String password;
    private Role role;
    private String avatar;

    public User(){
        super();
    }

    public User(String name, String surname, String login, String email, String password, Role role, String avatar) {
        super();
        this.name = name;
        this.surname = surname;
        this.login = login;
        this.email = email;
        this.password = password;
        this.role = role;
        this.avatar = avatar;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return String.format("User [" +
                "id=%s, " +
                "name=%s, " +
                "surname=%s, " +
                "login=%s " +
                "role=%s]", id, name, surname, login, role, avatar);
    }


}
