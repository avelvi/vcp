package com.aivlev.vcp.dto;

/**
 * Created by aivlev on 6/14/16.
 */
public class UpdatePasswordDto {
    private String newPassword;

    public UpdatePasswordDto() {
    }

    public UpdatePasswordDto(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
