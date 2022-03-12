package com.example.knotejava.service.impl;

import com.example.knotejava.config.NoteProperties;
import com.example.knotejava.entity.Note;
import com.example.knotejava.repository.NotesRepository;
import com.example.knotejava.service.NoteService;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NotesRepository notesRepository;

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();
    private final NoteProperties properties;

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
    public void uploadImage(MultipartFile file, String description, Model model) throws IOException {
        File uploadsDir = new File(properties.getUploadDir());
        if (!uploadsDir.exists() && !uploadsDir.mkdirs()) {
            throw new FileNotFoundException("Unable to create directory");
        }

        if (file != null && StringUtils.hasLength(file.getOriginalFilename())) {
            String fileId = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(file.getOriginalFilename());
            file.transferTo(new File(properties.getUploadDir() + fileId));
            model.addAttribute("description", description + " ![](/uploads/" + fileId + ")");
        }
    }
}
