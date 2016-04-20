package com.aivlev.vcp.service.impl;

/**
 * Created by aivlev on 4/20/16.
 */

import com.aivlev.vcp.model.Role;
import com.aivlev.vcp.model.User;
import com.aivlev.vcp.model.Video;
import com.aivlev.vcp.service.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TestDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestDataService.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private VideoService videoService;

    @Value("${media.dir}")
    private String mediaDir;

    @Value("${mongo.recreate.db}")
    private boolean mondoRecreateDb;

    private String reason = "mongo.recreate.db=true";

    @PostConstruct
    public void createTestDataIfNecessary() {
        boolean shouldTestDataBeCreated = shouldTestDataBeCreated();
        if (shouldTestDataBeCreated) {
            LOGGER.info("Detected create test data command: " + reason);
            createTestData();
        } else {
            LOGGER.info("Mongo db exists");
        }
    }

    private boolean shouldTestDataBeCreated() {
        boolean createTestData = mondoRecreateDb;
        if (!createTestData) {
            if (!mongoTemplate.collectionExists(User.class)) {
                reason = "Collection account not found";
                return true;
            } else if (!mongoTemplate.collectionExists(Video.class)) {
                reason = "Collection video not found";
                return true;
            }
        }
        return createTestData;
    }

    private void createTestData() {
        clearMediaSubFolders();
        clearCollections();

        List<User> users = createUsers();
        createVideos(users);
        LOGGER.info("Test mongo db created successfully");
    }

    private void clearMediaSubFolders() {
        for (File f : new File(mediaDir + "/thumbnails").listFiles()) {
            f.delete();
        }
        for (File f : new File(mediaDir + "/video").listFiles()) {
            f.delete();
        }
        LOGGER.info("Media sub folders cleared");
    }

    private void clearCollections() {
        mongoTemplate.remove(new Query(), User.class);
        mongoTemplate.remove(new Query(), Video.class);
    }

    private List<User> createUsers() {
        List<User> users = getUsers();
        for (User user : users) {
            mongoTemplate.insert(user);
        }
        LOGGER.info("Created {} test users", users.size());
        return users;
    }

    private List<User> getUsers() {
        return new ArrayList<>(Arrays.asList(
                new User("Tim", "Surname 1", "login1", "test1@test.ua", "12345", Role.ADMIN, "https://s3.amazonaws.com/uifaces/faces/twitter/sauro/128.jpg"),
                new User("Ron", "Surname 2", "login2", "test2@test.ua", "12345", Role.USER, "https://s3.amazonaws.com/uifaces/faces/twitter/k/128.jpg"),
                new User("Alex", "Surname 3", "login3", "test3@test.ua", "12345", Role.USER, "https://s3.amazonaws.com/uifaces/faces/twitter/marcosmoralez/128.jpg")));
    }

    private void createVideos(List<User> accounts) {
        List<Video> videos = new ArrayList<>();
        for (String videoLink : getVideoLinks()) {
            Video video = videoService.processVideo(new URLMultipartFile(videoLink));
            video.setOwner(accounts.remove(0));
            videos.add(video);
        }
        mongoTemplate.insert(videos, Video.class);
        LOGGER.info("Created {} video files", videos.size());
    }

    private String[] getVideoLinks() {
        String[] videoLinks = { "https://www.dropbox.com/s/r4ijmbn0bnxfxp0/Build%20a%20Simple%20Android%20App.mp4?dl=1",
                "https://www.dropbox.com/s/w8xy8hjhugvhibt/How%20the%20Internet%20Works.mp4?dl=1",
                "https://www.dropbox.com/s/088f5bn54g3pwqx/Learn%20Functional%20Python%20at%20Treehouse.mp4?dl=1" };
        return videoLinks;
    }

    private static class URLMultipartFile implements MultipartFile {
        private final String url;

        public URLMultipartFile(String url) {
            super();
            this.url = url;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getOriginalFilename() {
            return null;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public long getSize() {
            return 0;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return null;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return null;
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            try (InputStream in = new URL(url).openStream()) {
                Files.copy(in, Paths.get(dest.getAbsolutePath()));
            }
        }
    }
}
