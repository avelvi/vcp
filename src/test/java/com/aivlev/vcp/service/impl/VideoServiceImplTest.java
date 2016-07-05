package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.config.TestMockConfig;
import com.aivlev.vcp.exception.AccessDeniedException;
import com.aivlev.vcp.exception.ModelNotFoundException;
import com.aivlev.vcp.exception.ProcessMediaContentException;
import com.aivlev.vcp.model.User;
import com.aivlev.vcp.model.Video;
import com.aivlev.vcp.repository.search.VideoSearchRepository;
import com.aivlev.vcp.repository.storage.UserRepository;
import com.aivlev.vcp.repository.storage.VideoRepository;
import com.aivlev.vcp.service.UserService;
import com.aivlev.vcp.service.VideoService;
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
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Created by aivlev on 6/9/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestMockConfig.class})
public class VideoServiceImplTest {

    @Autowired
    private VideoService videoService;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VideoSearchRepository videoSearchRepository;

    @Autowired
    private UserService userService;

    @Value("${media.dir}")
    private String mediaDir;

    @Mock
    private Pageable pageable;

    @Mock
    private Page<Video> videoPage;

    @Mock
    private Video video;

    @Mock
    private Path path;

    @Mock
    private User user;

    @Mock
    private User user2;

    private String userId = "userId";

    private String userId2 = "userId2";

    private String userLogin = "someLogin";

    private String videoId = "videoId";

    @Before
    public void setUp() throws Exception {
        reset(videoRepository);
        reset(videoSearchRepository);
        clearMediaSubFolders();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindAllVideosByOwnerId(){
        when(videoRepository.findByOwnerId(userId, pageable)).thenReturn(videoPage);
        videoService.findAllVideosByOwnerId(userId, pageable);
        verify(videoRepository).findByOwnerId(userId, pageable);
    }

    @Test
    public void testFindAll(){
        when(videoRepository.findAll(pageable)).thenReturn(videoPage);
        videoService.findAll(pageable);
        verify(videoRepository).findAll(pageable);
    }

    @Test
    public void testDeleteVideo(){
        when(videoRepository.findOne(videoId)).thenReturn(video);
        when(userRepository.findByLogin(userLogin)).thenReturn(user);
        when(userService.findByLogin(userLogin)).thenReturn(user);
        when(video.getOwner()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        videoService.deleteVideo(false, userLogin, videoId);
        verify(videoRepository, times(1)).delete(videoId);
    }

    @Test
    public void testDeleteVideoAsAdmin(){
        when(videoRepository.findOne(videoId)).thenReturn(video);
        videoService.deleteVideo(true, userLogin, videoId);
        verify(videoRepository, times(1)).delete(videoId);
    }

    @Test(expected = ModelNotFoundException.class)
    public void testDeleteVideoWithModelNotFound(){
        when(videoRepository.findOne(videoId)).thenReturn(null);
        videoService.deleteVideo(false, userLogin, videoId);
        verify(videoRepository, times(0)).delete(videoId);
    }

    @Test(expected = AccessDeniedException.class)
    public void testDeleteVideoWithAccessDeniedException(){
        when(videoRepository.findOne(videoId)).thenReturn(video);
        when(userRepository.findByLogin(userLogin)).thenReturn(user);
        when(userService.findByLogin(userLogin)).thenReturn(user);
        when(video.getOwner()).thenReturn(user2);
        when(user2.getId()).thenReturn(userId2);
        when(user.getId()).thenReturn(userId);
        videoService.deleteVideo(false, userLogin, videoId);
        verify(videoRepository, times(1)).delete(videoId);
    }

    @Test
    public void testUpdateVideo(){
        when(videoRepository.findOne(videoId)).thenReturn(video);
        when(userRepository.findByLogin(userLogin)).thenReturn(user);
        when(userService.findByLogin(userLogin)).thenReturn(user);
        when(video.getId()).thenReturn(videoId);
        when(video.getTitle()).thenReturn("title");
        when(video.getDescription()).thenReturn("description");
        when(video.getOwner()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        videoService.updateVideo(false, userLogin, videoId, video);
        verify(videoRepository, times(1)).save(video);
        verify(videoSearchRepository, times(1)).save(video);
    }

    @Test
    public void testUpdateVideoAsAdmin(){
        when(videoRepository.findOne(videoId)).thenReturn(video);
        when(video.getId()).thenReturn(videoId);
        when(video.getTitle()).thenReturn("title");
        when(video.getDescription()).thenReturn("description");
        videoService.updateVideo(true, userLogin, videoId, video);
        verify(videoRepository, times(1)).save(video);
        verify(videoSearchRepository, times(1)).save(video);
    }

    @Test(expected = ModelNotFoundException.class)
    public void testUpdateVideoWithModelNotFound(){
        when(videoRepository.findOne(videoId)).thenReturn(null);
        videoService.updateVideo(true, userLogin, videoId, video);
        verify(videoRepository, times(0)).save(video);
        verify(videoSearchRepository, times(0)).save(video);
    }

    @Test(expected = AccessDeniedException.class)
    public void testUpdateVideoWithAccessDeniedException(){
        when(videoRepository.findOne(videoId)).thenReturn(video);
        when(userRepository.findByLogin(userLogin)).thenReturn(user);
        when(userService.findByLogin(userLogin)).thenReturn(user);
        when(video.getId()).thenReturn(videoId);
        when(video.getTitle()).thenReturn("title");
        when(video.getDescription()).thenReturn("description");
        when(video.getOwner()).thenReturn(user2);
        when(user2.getId()).thenReturn(userId2);
        when(user.getId()).thenReturn(userId);
        videoService.updateVideo(false, userLogin, videoId, video);
        verify(videoRepository, times(0)).save(video);
        verify(videoSearchRepository, times(0)).save(video);
    }

    @Test
    public void testFindAllVideosBySearchQuery(){
        when(videoSearchRepository.search(any(SearchQuery.class))).thenReturn(videoPage);
        videoService.findAllVideosBySearchQuery("some title", pageable);
        verify(videoSearchRepository, times(1)).search(any(SearchQuery.class));
    }

    @Test
    public void testSaveVideo() throws IOException {
        File file = new File(mediaDir + "/test-video.mp4");
        String path = videoService.saveVideo(file.toPath());
        assertNotNull(path);
        File newFile = new File(path);
        assertNotNull(newFile);
    }

    @Test(expected = ProcessMediaContentException.class)
    public void testSaveVideoWithIOException(){
        File file = new File(mediaDir + "/file-not-exists.mp4");
        String path = videoService.saveVideo(file.toPath());
    }

    @Test
    public void testCount() throws Exception {
        when(videoRepository.count()).thenReturn(1l);
        videoService.count();
        verify(videoRepository).count();
    }

    private void clearMediaSubFolders() {
        for(File dir : getMediaDirs()) {
            for (File f : dir.listFiles()) {
                f.delete();
            }
        }
    }

    private File[] getMediaDirs(){
        return new File[] {new File(mediaDir + "/thumbnails"), new File(mediaDir + "/video")};
    }


}
