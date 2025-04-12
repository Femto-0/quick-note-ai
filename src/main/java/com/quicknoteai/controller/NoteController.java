package com.quicknoteai.controller;

import com.quicknoteai.model.Note;
import com.quicknoteai.service.OllamaService;
import com.quicknoteai.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private OllamaService ollamaService;

    @PostMapping
    public Note createNote(@RequestBody Note note) {
        String summary = null;
        int maxRetries = 3;
        int attempts = 0;

        while (attempts < maxRetries) {
            try {
                System.out.println("Calling my boy llama for summarization...");
                summary = ollamaService.getSummary(note.getContent()).block();
                System.out.println("Summary received.");
                note.setTimeStamp(LocalDateTime.now());
                break; // success
            } catch (HttpClientErrorException.TooManyRequests e) {
                attempts++;
                System.out.println("Too many requests, retrying in 2s... (Attempt " + attempts + ")");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }
        }

        if (summary != null) {
            note.setSummary(summary);
        } else {
            note.setSummary("Summary could not be generated (rate limit).");
        }

        return noteRepository.save(note);
    }
}
