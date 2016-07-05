package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.aop.UploadVideoTempStorage;
import com.aivlev.vcp.config.TestMockConfig;
import com.aivlev.vcp.model.UploadForm;
import com.aivlev.vcp.service.VideoProcessorService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;

import static org.mockito.Mockito.when;

/**
 * Created by aivlev on 6/9/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestMockConfig.class})
public class VideoProcessorServiceImplTest {

    @Autowired
    private VideoProcessorService videoProcessorService;

    @Autowired
    private UploadVideoTempStorage uploadVideoTempStorage;

    @Value("${media.dir}")
    private String mediaDir;

    @Mock
    private UploadForm uploadForm;

    @Mock
    private MultipartFile multipartFile;

    @Before
    public void setUp() throws Exception {
        clearMediaSubFolders();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcessVideo(){
        Path path = new File(mediaDir + "/test-video.mp4").toPath();
        when(uploadForm.getFile()).thenReturn(multipartFile);
        when(uploadForm.getTitle()).thenReturn("title");
        when(uploadForm.getDescription()).thenReturn("description");
        when(uploadVideoTempStorage.getTempUploadedVideoPath()).thenReturn(path);
        videoProcessorService.processVideo(uploadForm);
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
