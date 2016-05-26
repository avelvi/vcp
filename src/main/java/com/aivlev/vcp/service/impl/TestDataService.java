package com.aivlev.vcp.service.impl;

/**
 * Created by aivlev on 4/20/16.
 */

import com.aivlev.vcp.model.Authority;
import com.aivlev.vcp.model.UploadForm;
import com.aivlev.vcp.model.User;
import com.aivlev.vcp.model.Video;
import com.aivlev.vcp.service.UserService;
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
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class TestDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestDataService.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserService userService;

    @Value("${media.dir}")
    private String mediaDir;

    @Value("${mongo.recreate.db}")
    private boolean mongoRecreateDb;

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
        boolean createTestData = mongoRecreateDb;
        if (!createTestData) {
            if (!mongoTemplate.collectionExists(User.class)) {
                reason = "Collection user not found";
                return true;
            } else if (!mongoTemplate.collectionExists(Video.class)) {
                reason = "Collection video not found";
                return true;
            } else if (!mongoTemplate.collectionExists(Authority.class)) {
                reason = "Collection authority not found";
                return true;
            }
        }
        return createTestData;
    }

    private void createTestData() {
        createMediaDirsIfNecessary();
        clearMediaSubFolders();
        clearCollections();
        List<Authority> authorities = createAuthorities();
        List<User> accounts = createUsers(authorities);
        createVideos(accounts);
        LOGGER.info("Test mongo db created successfully");
    }

    private void createMediaDirsIfNecessary(){
        for(File dir : getMediaDirs()) {
            if(!dir.exists()) {
                if(dir.mkdirs()){
                    LOGGER.info("Created media dir: "+dir.getAbsolutePath());
                } else {
                    LOGGER.error("Can't create media dir: "+dir.getAbsolutePath());
                }
            }
        }
    }

    private File[] getMediaDirs(){
        return new File[] {new File(mediaDir + "/thumbnails"), new File(mediaDir + "/video")};
    }

    private void clearMediaSubFolders() {
        for(File dir : getMediaDirs()) {
            for (File f : dir.listFiles()) {
                f.delete();
            }
        }
        LOGGER.info("Media sub folders cleared");
    }

    private void clearCollections() {
        mongoTemplate.remove(new Query(), User.class);
        mongoTemplate.remove(new Query(), Video.class);
    }

    private List<Authority> getAuthorities() {
        return new ArrayList<>(Arrays.asList(
                new Authority("admin"),
                new Authority("user")
                ));
    }

    private List<User> getUsers() {

        return new ArrayList<>(Arrays.asList(
                new User("Tim", "Surname 1", "login1", "test1@test.ua", "12345",
//                        new HashSet<Authority>(Arrays.asList("admin")),
                        "https://s3.amazonaws.com/uifaces/faces/twitter/sauro/128.jpg", null, true),
                new User("Ron", "Surname 2", "login2", "test2@test.ua", "12345",
//                        Role.USER,
                        "https://s3.amazonaws.com/uifaces/faces/twitter/k/128.jpg", null, true),
                new User("Alex", "Surname 3", "login3", "test3@test.ua", "12345",
//                        Role.USER,
                        "https://s3.amazonaws.com/uifaces/faces/twitter/marcosmoralez/128.jpg", null, true)));
    }

    private List<User> createUsers(List<Authority> authorities) {
        List<User> users = getUsers();
        int i = 0;
        for (User user : users) {
            if(i == 2){
                user.setAuthorities(authorities);
            } else {
                Iterator<Authority> it = authorities.iterator();
                while (it.hasNext()){
                    Authority authority = it.next();
                    if(authority.getName().equals("user")){
                        user.setAuthorities(Arrays.asList(authority));
                        break;
                    }
                }
            }
            userService.save(null, user);
            i++;
        }
        LOGGER.info("Created {} test users", users.size());
        return users;
    }

    private List<Authority> createAuthorities() {
        List<Authority> authorities = getAuthorities();
        for (Authority authority : authorities) {
            mongoTemplate.insert(authority);
        }
        LOGGER.info("Created {} test authority", authorities.size());
        return authorities;
    }

    private void createVideos(List<User> accounts) {
        int count = 0;
        List<File> tempFiles = null;
        Random r = new Random();
        try {
            tempFiles = getTempVideoFilesDownloadedFromInternet();
            for(int i = 0; i < 50; i++) {
                User owner = accounts.get(r.nextInt(accounts.size()));
                userService.uploadVideo(owner.getLogin(), new UploadForm("Title " + i, "Description " + i, new CopyTempFile(tempFiles.get(r.nextInt(tempFiles.size())))), null);
                count++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            LOGGER.info("Created {} video files", count);
            if(tempFiles != null) {
                for(File f : tempFiles) {
                    f.delete();
                }
            }
        }
    }

    private String[] getTestVideoLinks() {
        String[] videoLinks = { "https://www.dropbox.com/s/r4ijmbn0bnxfxp0/Build%20a%20Simple%20Android%20App.mp4?dl=1",
                "https://www.dropbox.com/s/w8xy8hjhugvhibt/How%20the%20Internet%20Works.mp4?dl=1",
                "https://www.dropbox.com/s/088f5bn54g3pwqx/Learn%20Functional%20Python%20at%20Treehouse.mp4?dl=1" };
        return videoLinks;
    }

    private void downloadFile(File file, String url) throws IOException {
        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, Paths.get(file.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private List<File> getTempVideoFilesDownloadedFromInternet() throws IOException{
        List<File> list = new ArrayList<>();
        try {
            for(String link : getTestVideoLinks()) {
                File temp = File.createTempFile("test", ".video");
                downloadFile(temp, link);
                list.add(temp);
            }
            return list;
        } catch (IOException e) {
            for(File f : list) {
                f.delete();
            }
            throw e;
        }
    }

    private static class CopyTempFile implements MultipartFile {
        private final File tempFile;

        public CopyTempFile(File tempFile) {
            super();
            this.tempFile = tempFile;
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
            Files.copy(Paths.get(tempFile.getAbsolutePath()), Paths.get(dest.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
