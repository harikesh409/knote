package com.example.knotejava.service;

import java.io.IOException;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

public interface NoteService {
    void getAllNotes(Model model);

    void saveNote(String description, Model model);

    void uploadImage(MultipartFile file, String description, Model model) throws IOException;
}
