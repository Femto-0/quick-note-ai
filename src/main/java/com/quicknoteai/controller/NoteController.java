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

//    @PostMapping
//    public Note processNote(@RequestBody Note note) {
//        String summary = null;
//        String mood = null;
//
//        // Try generating the summary
//        try {
//            System.out.println("Calling my boy llama for summarization...");
//            summary = ollamaService.getSummary(note.getContent()).block();
//            System.out.println("Summary received.");
//        } catch (HttpClientErrorException.TooManyRequests e) {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException ignored) {}
//        }
//
//        // Try determining the mood
//        try {
//            System.out.println("Calling my boy llama to check the vibe...");
//            mood = ollamaService.determineMood(note.getContent()).block();
//            System.out.println("Vibe received.");
//        } catch (HttpClientErrorException.TooManyRequests e) {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException ignored) {}
//        }
//
//        // Set the timestamp
//        note.setTimeStamp(LocalDateTime.now());
//
//        // Set summary and mood if available
//        note.setSummary(summary != null ? summary : "Summary could not be generated.");
//        note.setMood(mood != null ? mood : "Vibe couldn't be determined");
//
//        return noteRepository.save(note);
//    }


    /*
    combining result from summary and the mood and returning a json.
     */
    @PostMapping
    public NoteResponse processNote(@RequestBody Note note) {
        String summary;
        String mood;

        try {
            summary = ollamaService.getSummary(note.getContent()).block();
        } catch (Exception e) {
            summary = "Summary could not be generated.";
        }

        try {
            mood = ollamaService.determineMood(note.getContent()).block();
        } catch (Exception e) {
            mood = "Unable to determine mood, are you a Robot by any chance?";
        }

        note.setSummary(summary);
        note.setMood(mood);
        note.setTimeStamp(LocalDateTime.now());
        noteRepository.save(note); // Save the full note

        return new NoteResponse(summary, mood); // Return only summary and mood
    }
}
