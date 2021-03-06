package com.aivlev.vcp.controller;

import com.aivlev.vcp.dto.UpdatePasswordDto;
import com.aivlev.vcp.model.Category;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by aivlev on 5/1/16.
 */
@RestController
@RequestMapping(value = "/users")
public class UserController extends GenericController{

    @Autowired
    UserService userService;

    @PreAuthorize("hasAuthority('admin')")
    @RequestMapping(method = RequestMethod.GET)
    public Object getAllUsers(@PageableDefault(size = 5)Pageable pageable){
        Page<User> result = userService.findAllUsers(pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('admin')")
    @RequestMapping(method = RequestMethod.POST)
    public void createUser(@RequestBody User user){
        userService.registerUser(user, false);
    }

    @PreAuthorize("hasAuthority('user')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object getUser(@PathVariable(value = "id") String id,
                          @AuthenticationPrincipal UserDetails userDetails){
        User user = userService.findUser(isAdmin(userDetails), userDetails.getUsername(), id);
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
    @RequestMapping(value = "/{id}/changeAvatar", method = RequestMethod.POST)
    public ResponseEntity<String> changeAvatar(@PathVariable(value = "id") String id,
            @AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam("file") MultipartFile file
                            ){
        UploadForm uploadForm = new UploadForm(file);
        String path = userService.changeAvatar(userDetails.getUsername(), uploadForm);
//        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(details.getId())
//                .toUri();
        return new ResponseEntity<>(path, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/updatePassword", method = RequestMethod.POST)
    public void updatePassword(@PathVariable(value = "id") String id,
                               @RequestBody UpdatePasswordDto updatePasswordDto,
                               @AuthenticationPrincipal UserDetails userDetails){
        userService.updatePassword(id, updatePasswordDto, userDetails.getUsername());
    }

    @PreAuthorize("hasAuthority('user')")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public void uploadVideo(@AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam("file") MultipartFile file,
                            @RequestParam("title") String title,
                            @RequestParam("description") String description,
                            @RequestParam("categoryId") String id,
                            @RequestParam("categoryName") String name
    ){
        Category category = new Category(id, name);
        UploadForm uploadForm = new UploadForm(title, description, file);
        userService.uploadVideo(userDetails.getUsername(), uploadForm, category);
    }
}
