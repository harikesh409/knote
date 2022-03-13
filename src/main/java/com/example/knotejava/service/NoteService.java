package com.example.knotejava.service;

import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

public interface NoteService {
    void getAllNotes(Model model);

    void saveNote(String description, Model model);

    void uploadImage(MultipartFile file, String description, Model model) throws Exception;

    byte[] getImageByName(String name) throws Exception;
}
