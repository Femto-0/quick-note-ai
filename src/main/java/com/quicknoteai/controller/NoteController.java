package com.quicknoteai.controller;

import com.quicknoteai.model.Note;
import com.quicknoteai.service.OllamaService;
import com.quicknoteai.repository.NoteRepository;
import com.quicknoteai.dto.NoteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private OllamaService ollamaService;

    /*
    combining result from summary and the mood and returning a json.
     */
    @PostMapping
    public NoteResponse processNote(@RequestBody Note note) {
        String summary;
        String mood;

        try {
            System.out.println("Requesting llama to generate summary....");
            summary = ollamaService.getSummary(note.getContent()).block();
            System.out.println("Summary received");
        } catch (Exception e) {
            summary = "Summary could not be generated.";
        }

        try {
            System.out.println("Determining mood....");
            mood = ollamaService.determineMood(note.getContent()).block();
            System.out.println("Mood determined");
        } catch (Exception e) {
            mood = "Unable to determine mood, are you a Robot by any chance?";
        }

        note.setSummary(summary);
        note.setMood(mood);
        note.setTimeStamp(LocalDateTime.now());
        noteRepository.save(note); // Save the full note

        return new NoteResponse(summary, mood); // Return only summary and mood
    }


    //TODO: logic to fetch the notes stored in the db
    @GetMapping
    public void loadNotes(@RequestBody Note note){

    }
}
