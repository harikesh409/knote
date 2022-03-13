package com.example.knotejava.service.impl;

import com.example.knotejava.config.NoteProperties;
import com.example.knotejava.entity.Note;
import com.example.knotejava.repository.NotesRepository;
import com.example.knotejava.service.NoteService;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableConfigurationProperties(NoteProperties.class)
public class NoteServiceImpl implements NoteService {

    private final NotesRepository notesRepository;

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();
    private final NoteProperties properties;

    private MinioClient minioClient;

    @PostConstruct
    public void init() throws InterruptedException {
        initMinio();
    }

    private void initMinio() throws InterruptedException {
        boolean success = false;
        while (!success) {
            try {

                minioClient = MinioClient.builder()
                        .endpoint(properties.getMinioSchema() + "://" + properties.getMinioHost() + ":" + properties.getMinioPort())
                        .credentials(properties.getMinioAccessKey(), properties.getMinioSecretKey())
                        .build();

                // Check if the bucket already exists.
                BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder()
                        .bucket(properties.getMinioBucket())
                        .build();
                boolean isExist = minioClient.bucketExists(bucketExistsArgs);
                if (isExist) {
                    log.debug("> Bucket already exists.");
                } else {
                    log.debug("Creating bucket.");
                    MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder()
                            .bucket(properties.getMinioBucket())
                            .build();
                    minioClient.makeBucket(makeBucketArgs);
                    log.info("Bucket created.");
                }
                success = true;
            } catch (Exception e) {
                log.error("Error while connecting to Minio", e);
                log.info("> Minio Reconnect: {}", properties.isMinioReconnectEnabled());
                if (properties.isMinioReconnectEnabled()) {
                    Thread.sleep(5000);
                } else {
                    success = true;
                }
            }
        }
        log.info("> Minio initialized!");
    }


    @Override
    public void getAllNotes(Model model) {
        List<Note> notes = notesRepository.findAll();
        Collections.reverse(notes);
        model.addAttribute("notes", notes);
    }

    @Override
    public void saveNote(String description, Model model) {
        if (StringUtils.hasLength(description)) {
            Node document = parser.parse(description.trim());
            String html = renderer.render(document);
            notesRepository.save(new Note(null, html));
            //After publish you need to clean up the textarea
            model.addAttribute("description", "");
        }
    }

    @Override
    public void uploadImage(MultipartFile file, String description, Model model) throws Exception {
        if (file != null && StringUtils.hasLength(file.getOriginalFilename())) {
            String fileId = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(file.getOriginalFilename());
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(properties.getMinioBucket())
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .object(fileId)
                    .build();
            minioClient.putObject(putObjectArgs);
            model.addAttribute("description",
                    description + " ![](/img/" + fileId + ")");
        }
    }

    @Override
    public byte[] getImageByName(String name) throws Exception {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(properties.getMinioBucket())
                .object(name)
                .build();
        InputStream imageStream = minioClient.getObject(getObjectArgs);
        return StreamUtils.copyToByteArray(imageStream);
    }
}
