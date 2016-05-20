package com.aivlev.vcp.service;

import com.aivlev.vcp.dto.ResetPasswordDto;

/**
 * Created by aivlev on 5/19/16.
 */
public interface ForgotPasswordService {

    void sendRecoveryEmail(String email);

    void updatePassword(ResetPasswordDto resetPasswordDto);

    void validateCode(String code);
}
