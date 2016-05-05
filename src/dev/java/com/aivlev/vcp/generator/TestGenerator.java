package com.aivlev.vcp.generator;

import com.aivlev.vcp.config.MongoConfig;
import com.aivlev.vcp.config.ServiceConfig;
import com.aivlev.vcp.model.Role;
import com.aivlev.vcp.model.User;
import com.aivlev.vcp.model.Video;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.FileChannelWrapper;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by aivlev on 4/19/16.
 */
public class TestGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestGenerator.class);

    private static final String[] VIDEO_LINKS = {
            "https://www.dropbox.com/s/r4ijmbn0bnxfxp0/Build%20a%20Simple%20Android%20App.mp4?dl=1",
            "https://www.dropbox.com/s/w8xy8hjhugvhibt/How%20the%20Internet%20Works.mp4?dl=1",
            "https://www.dropbox.com/s/088f5bn54g3pwqx/Learn%20Functional%20Python%20at%20Treehouse.mp4?dl=1"
    };

    private static final Random RANDOM = new Random();

    public static void main(String[] args) throws Exception {
        try(AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext()){
            ctx.register(ServiceConfig.class);
            ctx.register(MongoConfig.class);
            ctx.refresh();

            MongoOperations mongoOps = ctx.getBean(MongoOperations.class);

            mongoOps.remove(new Query(), User.class);
            mongoOps.remove(new Query(), Video.class);

            clearMediaSubFolders();

            List<User> users = buildAccounts();
            for(User user : users) {
                mongoOps.insert(user);
            }
            List<Video> videos = buildVideo(users);
            mongoOps.insert(videos, Video.class);

            LOGGER.info("Insert succesful");
        }
    }

    private static void clearMediaSubFolders() {
        for (File f : new File("src/main/webapp/media/thumbnails").listFiles()) {
            f.delete();
        }
        for (File f : new File("src/main/webapp/media/video").listFiles()) {
            f.delete();
        }
        LOGGER.info("Media sub folders cleared");
    }

    private static List<User> buildAccounts() {
        // http://uifaces.com/
        return Arrays.asList(
                new User("Tim", "Surname 1", "login1", "test1@test.ua", "12345",
//                        Role.ADMIN,
                        "https://s3.amazonaws.com/uifaces/faces/twitter/sauro/128.jpg"),
                new User("Ron", "Surname 2", "login2", "test2@test.ua", "12345",
//                        Role.USER,
                        "https://s3.amazonaws.com/uifaces/faces/twitter/k/128.jpg"),
                new User("Alex", "Surname 3", "login3", "test3@test.ua", "12345",
//                        Role.USER,
                        "https://s3.amazonaws.com/uifaces/faces/twitter/marcosmoralez/128.jpg"));
    }

    private static List<Video> buildVideo(List<User> users) throws IOException, JCodecException {
        List<Video> videos = new ArrayList<Video>();
        int i = 1;
        for (String videoLink : VIDEO_LINKS) {

            String uid = UUID.randomUUID().toString() + ".mp4";
            File destVideo = new File("src/main/webapp/media/video", uid);

            try (InputStream in = new URL(videoLink).openStream()) {
                Files.copy(in, Paths.get(destVideo.getAbsolutePath()));
            }

            List<String> thumbnails = createThumbnails(destVideo);
            videos.add(new Video("Title" + i, "Description" + i, String.valueOf(new Date().getTime()), "/media/video/" + uid, thumbnails, users.get(RANDOM.nextInt(users.size()))));
            LOGGER.info("Video {} processed", destVideo.getName());
            i++;
        }
        return videos;
    }

    private static List<String> createThumbnails(File destVideo) throws IOException, JCodecException {
        List<String> thumbnails = new ArrayList<String>();
        FrameGrab grab = new FrameGrab(
                new FileChannelWrapper(FileChannel.open(Paths.get(destVideo.getAbsolutePath()))));
        for (int i = 0; i < RANDOM.nextInt(15) + 3; i++) {
            Picture nativeFrame = grab.seekToSecondPrecise(i * 3 + i).getNativeFrame();
            if (nativeFrame != null) {
                BufferedImage img = AWTUtil.toBufferedImage(nativeFrame);
                String uid = UUID.randomUUID() + ".jpg";
                ImageIO.write(img, "jpg", new File("src/main/webapp/media/thumbnails", uid));
                thumbnails.add("/media/thumbnails/" + uid);
            }
        }
        LOGGER.info("Created {} thumbnails for video {}", thumbnails.size(), destVideo.getName());
        return thumbnails;
    }
}
