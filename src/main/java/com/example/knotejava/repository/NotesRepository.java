package com.example.knotejava.repository;

import com.example.knotejava.entity.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotesRepository extends MongoRepository<Note, String> {
}
