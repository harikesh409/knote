package com.example.knotejava.controller;


import com.example.knotejava.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class NoteController {

    public static final String INDEX_PAGE_URL = "index";
    private final NoteService noteService;

    @GetMapping("/")
    public String index(Model model) {
        noteService.getAllNotes(model);
        return INDEX_PAGE_URL;
    }

    @PostMapping("/note")
    public String saveNotes(@RequestParam("image") MultipartFile file,
                            @RequestParam String description,
                            @RequestParam(required = false) String publish,
                            @RequestParam(required = false) String upload,
                            Model model) throws Exception {

        if (publish != null && publish.equals("Publish")) {
            noteService.saveNote(description, model);
            noteService.getAllNotes(model);
            return "redirect:/";
        }
        if (upload != null && upload.equals("Upload")) {
            noteService.uploadImage(file, description, model);
            noteService.getAllNotes(model);
            return INDEX_PAGE_URL;
        }
        // After save fetch all notes again
        return INDEX_PAGE_URL;
    }

    @GetMapping(value = "/img/{name}", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getImageByName(@PathVariable String name) throws Exception {
        return noteService.getImageByName(name);
    }
}
