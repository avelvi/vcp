package com.aivlev.vcp.dto;

/**
 * Created by aivlev on 4/28/16.
 */
public class UserDto {

    private String id;
    private String login;
    private String name;
    private String email;
    private String password;

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
}
