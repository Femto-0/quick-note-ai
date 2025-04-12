package com.quicknoteai.dto;

public class NoteResponse {
    private String summary;
    private String mood;

    // Constructors
    public NoteResponse() {}

    public NoteResponse(String summary, String mood) {
        this.summary = summary;
        this.mood = mood;
    }

    // Getters and setters
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }
}
