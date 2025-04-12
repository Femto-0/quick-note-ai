package com.quicknoteai.repository;

import com.quicknoteai.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
/*
Since we are extending JpaRepository, we will have basic CRUD functions available.
 */
public interface NoteRepository extends JpaRepository<Note, Long> {
}
