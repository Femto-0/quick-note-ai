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
    private String content;
    @Lob
    private String summary;

    // Getters and Setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
    public void setTimeStamp(LocalDateTime timestamp){
        this.timestamp = timestamp;
    }

}
