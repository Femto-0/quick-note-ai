package com.quicknoteai.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime timestamp;

    @Lob
    private String mood;
    @Lob
    private String content;
    @Lob
    private String summary;

    // Getters and Setters

    public void setTimeStamp(LocalDateTime timestamp){
        this.timestamp = timestamp;
    }
    public String getContent() {
        return content;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
    public void setMood(String mood) {
        this.mood = mood;
    }

}
