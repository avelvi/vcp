package com.aivlev.vcp.controller;

import com.aivlev.vcp.model.UploadForm;
import com.aivlev.vcp.model.User;
import com.aivlev.vcp.model.Video;
import com.aivlev.vcp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by aivlev on 5/1/16.
 */
@RestController
@RequestMapping(value = "/users")
public class UserController {

    private static final String ADMIN_ROLE = "admin";

    @Autowired
    UserService userService;

    @PreAuthorize("hasAuthority('admin')")
    @RequestMapping(method = RequestMethod.GET)
    public Object getAllUsers(@PageableDefault(size = 5)Pageable pageable){
        Page<User> result = userService.findAllUsers(pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object getUser(@PathVariable(value = "id") String id,
                          @AuthenticationPrincipal UserDetails userDetails,
                          SecurityContextHolderAwareRequestWrapper requestWrapper){
        User user = userService.findUser(requestWrapper.isUserInRole(ADMIN_ROLE), userDetails.getUsername(), id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public void updateUser(@PathVariable(value = "id") String id, @RequestBody User user){
        userService.updateUser(id, user);
    }

    @PreAuthorize("hasAuthority('admin')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable(value = "id") String id){
        userService.deleteUser(id);
    }

    @RequestMapping(value = "/{id}/videos", method = RequestMethod.GET)
    public Object getUserVideos(@PathVariable(value = "id") String id,
                                @PageableDefault(size = 12)Pageable pageable){
        Page<Video> result = userService.findVideos(id, pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user')")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public void uploadVideo(@AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam("file") MultipartFile file,
                            @RequestParam("title") String title,
                            @RequestParam("description") String description){
        UploadForm uploadForm = new UploadForm(title, description, file);
        userService.uploadVideo(userDetails.getUsername(), uploadForm);
    }



}
