package com.quicknoteai.controller;

import com.quicknoteai.model.Note;
import com.quicknoteai.service.OllamaService;
import com.quicknoteai.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Mono;

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
    @Async
    public Mono<Note> processNote(@RequestBody Note note) {
        Mono<String> summaryMono = ollamaService.getSummary(note.getContent())
                .onErrorReturn("Summary could not be generated.");

        Mono<String> moodMono = ollamaService.determineMood(note.getContent())
                .onErrorReturn("Vibe couldn't be determined");

        return Mono.zip(summaryMono, moodMono)
                .map(tuple -> {
                    note.setSummary(tuple.getT1());
                    note.setMood(tuple.getT2());
                    note.setTimeStamp(LocalDateTime.now());
                    return noteRepository.save(note); // this is okay here
                });
    }

}
