package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.aop.UploadVideoTempStorage;
import com.aivlev.vcp.config.TestMockConfig;
import com.aivlev.vcp.dto.UpdatePasswordDto;
import com.aivlev.vcp.exception.AccessDeniedException;
import com.aivlev.vcp.exception.CodeNotFoundException;
import com.aivlev.vcp.exception.DuplicateEntityException;
import com.aivlev.vcp.exception.ModelNotFoundException;
import com.aivlev.vcp.model.*;
import com.aivlev.vcp.repository.search.VideoSearchRepository;
import com.aivlev.vcp.repository.storage.AuthorityRepository;
import com.aivlev.vcp.repository.storage.UserRepository;
import com.aivlev.vcp.repository.storage.VideoRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;

import static org.mockito.Mockito.*;

/**
 * Created by aivlev on 6/9/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestMockConfig.class})
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoSearchRepository videoSearchRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private UploadVideoTempStorage uploadVideoTempStorage;

    @Value("${media.dir}")
    private String mediaDir;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private UploadForm uploadForm;

    @Mock
    private Category category;

    @Mock
    private User user;

    @Mock
    private User user2;

    @Mock
    private Video video;

    @Mock
    private Pageable pageable;

    @Mock
    private Page<Video> videoPage;

    @Mock
    private Page<User> userPage;

    @Mock
    private Authority authority;

    @Mock
    private UpdatePasswordDto updatePasswordDto;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUploadVideo(){
        Path path = new File(mediaDir + "/test-video.mp4").toPath();
        when(uploadForm.getFile()).thenReturn(multipartFile);
        when(uploadForm.getTitle()).thenReturn("title");
        when(uploadForm.getDescription()).thenReturn("description");
        when(uploadVideoTempStorage.getTempUploadedVideoPath()).thenReturn(path);
        when(userRepository.findByLogin(anyString())).thenReturn(user);
        userService.uploadVideo(anyString(), uploadForm, category);
        verify(videoRepository, times(1)).save(any(Video.class));
        verify(videoSearchRepository, times(1)).save(any(Video.class));
    }

    @Test(expected = ModelNotFoundException.class)
    public void testUploadVideoWithModelNotFoundException(){
        when(userRepository.findByLogin(anyString())).thenReturn(null);
        userService.uploadVideo(anyString(), uploadForm, category);
        verify(videoRepository, times(0)).save(any(Video.class));
        verify(videoSearchRepository, times(0)).save(any(Video.class));
    }

    @Test
     public void testFindByLogin(){
        when(userRepository.findByLogin(anyString())).thenReturn(user);
        userService.findByLogin(anyString());
        verify(userRepository, times(1)).findByLogin(anyString());
    }

    @Test(expected = ModelNotFoundException.class)
    public void testFindByLoginModelNotFoundException(){
        when(userRepository.findByLogin(anyString())).thenReturn(null);
        userService.findByLogin(anyString());
        verify(userRepository, times(0)).findByLogin(anyString());
    }

    @Test
    public void testFindByEmail(){
        when(userRepository.findByEmail(anyString())).thenReturn(user);
        userService.findByEmail(anyString());
        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test(expected = ModelNotFoundException.class)
    public void testFindByEmailModelNotFoundException(){
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        userService.findByEmail(anyString());
        verify(userRepository, times(0)).findByEmail(anyString());
    }

    @Test
    public void testSave(){
        when(user.getPassword()).thenReturn("password");
        userService.save(null, user);
        verify(userRepository, times(1)).save(user);
    }

    @Test(expected = ModelNotFoundException.class)
    public void testSaveWithModelNotFoundException(){
        when(userRepository.findOne(anyString())).thenReturn(null);
        userService.save(anyString(), user);
        verify(userRepository, times(0)).save(user);
    }

    @Test
     public void testFindUser(){
        when(userRepository.findByLogin(anyString())).thenReturn(user);
        when(user.getId()).thenReturn("id");
        userService.findUser(false, anyString(), "id");
        verify(userRepository, times(1)).findByLogin(anyString());
    }

    @Test
    public void testFindUserAsAdmin(){
        when(userRepository.findOne(anyString())).thenReturn(user);
        userService.findUser(true, anyString(), "id");
        verify(userRepository, times(1)).findOne(anyString());
    }

    @Test(expected = ModelNotFoundException.class)
    public void testFindUserWithModelNotFoundException(){
        when(userRepository.findByLogin(anyString())).thenReturn(null);
        userService.findUser(false, anyString(), "id");
        verify(userRepository, times(1)).findByLogin(anyString());
    }

    @Test(expected = ModelNotFoundException.class)
    public void testFindUserAsAdminWithModelNotFoundException(){
        when(userRepository.findOne(anyString())).thenReturn(null);
        userService.findUser(true, anyString(), "id");
        verify(userRepository, times(1)).findOne(anyString());
    }

    @Test(expected = AccessDeniedException.class)
    public void testFindUserAccessDeniedException(){
        String id = "id";
        when(userRepository.findByLogin(anyString())).thenReturn(user);
        when(user.getId()).thenReturn("");
        userService.findUser(false, anyString(), id);
        verify(userRepository, times(1)).findByLogin(anyString());
    }

    @Test
    public void testFindVideos(){
        when(videoRepository.findByOwnerId("", pageable)).thenReturn(videoPage);
        userService.findVideos("", pageable);
        verify(videoRepository, times(1)).findByOwnerId("", pageable);
    }

    @Test
    public void testFindAllUsers(){
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        userService.findAllUsers(pageable);
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testDeleteUser() throws Exception {
        when(userRepository.findOne(anyString())).thenReturn(user);
        userService.deleteUser(anyString());
        verify(userRepository, times(1)).delete(anyString());
    }

    @Test(expected = ModelNotFoundException.class)
    public void testDeleteUserWithException() throws Exception {
        when(userRepository.findOne(anyString())).thenReturn(null);
        userService.deleteUser(anyString());
        verify(userRepository, times(0)).delete(anyString());

    }

    @Test
    public void testUpdateUser(){
        when(userRepository.findOne(anyString())).thenReturn(user);
        when(userRepository.findByEmail(anyString())).thenReturn(user);
        when(user.getEmail()).thenReturn("test@testvcp.vcp");
        when(user.getLogin()).thenReturn("login");
        when(user.getPassword()).thenReturn("password");
        userService.updateUser(anyString(), user);
        verify(userRepository, times(1)).save(user);
    }

    @Test(expected = DuplicateEntityException.class)
    public void testUpdateUserWithDuplicateEntityException(){
        when(userRepository.findOne(anyString())).thenReturn(user2);
        when(userRepository.findByEmail(anyString())).thenReturn(user2);
        when(user.getEmail()).thenReturn("test@testvcp.vcp");
        when(user2.getEmail()).thenReturn("test2@testvcp.vcp");
        userService.updateUser(anyString(), user);
        verify(userRepository, times(0)).save(user);
    }

    @Test(expected = DuplicateEntityException.class)
    public void testUpdateUserWithDuplicateEntityException2(){
        when(userRepository.findOne(anyString())).thenReturn(user2);
        when(userRepository.findByLogin(anyString())).thenReturn(user2);
        when(user.getEmail()).thenReturn("test@testvcp.vcp");
        when(user2.getEmail()).thenReturn("test@testvcp.vcp");
        when(user.getLogin()).thenReturn("login");
        when(user2.getLogin()).thenReturn("login1");
        userService.updateUser(anyString(), user);
        verify(userRepository, times(0)).save(user);
    }

    @Test(expected = ModelNotFoundException.class)
    public void testUpdateUserWithModelNotFoundException(){
        when(userRepository.findOne(anyString())).thenReturn(null);
        userService.updateUser(anyString(), user);
        verify(userRepository, times(0)).save(user);
    }

    @Test
    public void testActivateUser(){
        String code = JWTUtils.generateCode(user);
        when(userRepository.findByLogin(anyString())).thenReturn(user);
        userService.activateUser(code);
        verify(userRepository, times(1)).save(user);
    }

    @Test(expected = ModelNotFoundException.class)
    public void testActivateUserWithModelNotFoundException(){
        String code = JWTUtils.generateCode(user);
        when(userRepository.findByLogin(anyString())).thenReturn(null);
        userService.activateUser(code);
        verify(userRepository, times(0)).save(user);
    }

    @Test(expected = CodeNotFoundException.class)
    public void testActivateUserWithCodeNotFoundException(){
        userService.activateUser("code");
        verify(userRepository, times(0)).save(user);
    }

    @Test
     public void testRegisterUser(){
        when(user.getLogin()).thenReturn("login");
        when(user.getEmail()).thenReturn("test@test.vcp");
        when(user.getPassword()).thenReturn("password");
        when(authorityRepository.findByName(anyString())).thenReturn(authority);
        when(userRepository.findByLogin(anyString())).thenReturn(null);
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        userService.registerUser(user, true);
        verify(userRepository, times(1)).save(user);
    }

    @Test(expected = DuplicateEntityException.class)
    public void testRegisterUserWithDuplicateEntityException(){
        when(user.getLogin()).thenReturn("login");
        when(userRepository.findByLogin(anyString())).thenReturn(user);
        userService.registerUser(user, true);
        verify(userRepository, times(0)).save(user);
    }

    @Test(expected = DuplicateEntityException.class)
    public void testRegisterUserWithDuplicateEntityException2(){
        when(user.getLogin()).thenReturn("login");
        when(user.getEmail()).thenReturn("test@test.vcp");
        when(userRepository.findByLogin(anyString())).thenReturn(null);
        when(userRepository.findByEmail(anyString())).thenReturn(user);
        userService.registerUser(user, true);
        verify(userRepository, times(0)).save(user);
    }

    @Test
    public void testUpdatePassword(){
        when(userRepository.findOne(anyString())).thenReturn(user);
        when(userRepository.findByLogin(anyString())).thenReturn(user);
        when(user.getId()).thenReturn("id");
        when(updatePasswordDto.getNewPassword()).thenReturn("password");
        userService.updatePassword("id", updatePasswordDto, "login");
        verify(userRepository, times(1)).save(user);
    }

    @Test(expected = ModelNotFoundException.class)
    public void testUpdatePasswordWithModelNotFoundException(){
        userService.updatePassword(null, updatePasswordDto, "login");
        verify(userRepository, times(0)).save(user);
    }
    @Test(expected = AccessDeniedException.class)
    public void testUpdatePasswordWithAccessDeniedException(){
        when(userRepository.findOne(anyString())).thenReturn(user);
        when(userRepository.findByLogin(anyString())).thenReturn(null);
        userService.updatePassword("id", updatePasswordDto, "login");
        verify(userRepository, times(0)).save(user);
    }

    @After
    public void tearDown() throws Exception {
        reset(videoRepository);
        reset(videoSearchRepository);
        reset(userRepository);
    }

}
