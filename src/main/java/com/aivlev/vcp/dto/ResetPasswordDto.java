package com.aivlev.vcp.dto;

/**
 * Created by aivlev on 5/20/16.
 */
public class ResetPasswordDto {

    private String code;
    private String password;

    public ResetPasswordDto() {
    }

    public ResetPasswordDto(String code, String password) {
        this.code = code;
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
