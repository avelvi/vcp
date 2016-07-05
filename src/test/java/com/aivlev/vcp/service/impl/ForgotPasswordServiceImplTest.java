package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.config.TestMockConfig;
import com.aivlev.vcp.dto.ResetPasswordDto;
import com.aivlev.vcp.exception.CodeNotFoundException;
import com.aivlev.vcp.exception.ModelNotFoundException;
import com.aivlev.vcp.model.User;
import com.aivlev.vcp.repository.storage.UserRepository;
import com.aivlev.vcp.service.ForgotPasswordService;
import com.aivlev.vcp.service.UserService;
import com.aivlev.vcp.utils.JWTUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.*;

/**
 * Created by aivlev on 6/10/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestMockConfig.class})
public class ForgotPasswordServiceImplTest {

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Mock
    private User user;

    @Mock
    private ResetPasswordDto resetPasswordDto;

    @Value("${email.fromEmail}")
    private String email;

    @Before
    public void setUp() throws Exception {
        reset(userRepository);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSendRecoveryEmail(){
        when(user.getLogin()).thenReturn("login");
        when(user.getEmail()).thenReturn(email);
        when(user.getId()).thenReturn("id");
        when(user.getPassword()).thenReturn("password");
        when(userRepository.findByEmail(anyString())).thenReturn(user);
        when(userRepository.findOne(anyString())).thenReturn(user);
        forgotPasswordService.sendRecoveryEmail(anyString());
    }

    @Test
    public void testValidateCode(){
        when(user.getLogin()).thenReturn("login");
        when(userRepository.findByLogin(anyString())).thenReturn(user);
        String code = JWTUtils.generateCode(user);
        when(user.getRecoveryCode()).thenReturn(code);
        forgotPasswordService.validateCode(code);
    }
    @Test(expected = CodeNotFoundException.class)
    public void testValidateCodeWithCodeNotFoundException(){
        when(user.getLogin()).thenReturn("login");
        when(userRepository.findByLogin(anyString())).thenReturn(user);
        String code = JWTUtils.generateCode(user);
        when(user.getRecoveryCode()).thenReturn(anyString());
        forgotPasswordService.validateCode(code);
    }

    @Test(expected = CodeNotFoundException.class)
    public void testValidateCodeWithCodeNotFoundException2(){
        forgotPasswordService.validateCode("code");
    }

    @Test(expected = ModelNotFoundException.class)
    public void testValidateCodeWithModelNotFoundException(){
        when(user.getLogin()).thenReturn("login");
        when(userRepository.findByLogin(anyString())).thenReturn(null);
        String code = JWTUtils.generateCode(user);
        forgotPasswordService.validateCode(code);
    }

    @Test
     public void testUpdatePassword(){
        String code = JWTUtils.generateCode(user);
        when(resetPasswordDto.getCode()).thenReturn(code);
        when(userRepository.findByLogin(anyString())).thenReturn(user);
        when(user.getRecoveryCode()).thenReturn(code);
        when(user.getId()).thenReturn("");
        when(resetPasswordDto.getPassword()).thenReturn("password");
        when(user.getPassword()).thenReturn("password");
        when(userRepository.findOne(anyString())).thenReturn(user);
        forgotPasswordService.updatePassword(resetPasswordDto);
        verify(userRepository, times(1)).save(any(User.class));

    }

    @Test(expected = CodeNotFoundException.class)
    public void testUpdatePasswordWithCodeNotFoundException(){
        String code = JWTUtils.generateCode(user);
        when(userRepository.findByLogin(anyString())).thenReturn(user);
        when(user.getRecoveryCode()).thenReturn(null);
        when(resetPasswordDto.getCode()).thenReturn(code);
        forgotPasswordService.updatePassword(resetPasswordDto);
    }

    @Test(expected = CodeNotFoundException.class)
    public void testUpdatePasswordWithCodeNotFoundException2(){
        when(resetPasswordDto.getCode()).thenReturn("code");
        forgotPasswordService.updatePassword(resetPasswordDto);
    }
}
