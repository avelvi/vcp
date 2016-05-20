package com.aivlev.vcp.controller;

import com.aivlev.vcp.dto.ResetPasswordDto;
import com.aivlev.vcp.dto.UserDto;
import com.aivlev.vcp.model.User;
import com.aivlev.vcp.security.SecurityUtils;
import com.aivlev.vcp.service.ForgotPasswordService;
import com.aivlev.vcp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Created by aivlev on 4/28/16.
 */
@RestController
public class AuthController {

    @Autowired
    UserService userService;

    @Autowired
    ForgotPasswordService forgotPasswordService;

    @PreAuthorize("hasAuthority('user')")
    @RequestMapping(value = "/security/user", method = RequestMethod.GET)
    public Object getUser(){
        User user = userService.findByLogin(SecurityUtils.getCurrentLogin());
        if(null != user){
            user.setPassword(null);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void register(@RequestBody UserDto userDto){

        userService.registerUser(userDto);
    }

    @RequestMapping(value = "/activate/code/{code:.+}", method = RequestMethod.GET)
    public void activate(@PathVariable(value = "code") String code){
        userService.activateUser(code);
    }

    @RequestMapping(value = "/restore/{email:.+}", method = RequestMethod.POST)
    public void restore(@PathVariable String email){
        forgotPasswordService.sendRecoveryEmail(email);
    }

    @RequestMapping(value = "/validate/code/{code:.+}", method = RequestMethod.GET)
    public void validate(@PathVariable(value = "code") String code){
        forgotPasswordService.validateCode(code);
    }

    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    public void updatePassword(@RequestBody ResetPasswordDto resetPasswordDto){
        forgotPasswordService.updatePassword(resetPasswordDto);
    }




}
