# QUICK NOTE AI

Quicknote AI is a note-taking web app that summarizes the note you just took and saves it alongside your original note!

It also comes with features such as:
1. Speech-to-text  
2. Text-to-speech  
3. Mood tracker  

---

## Requirements

- Java 21  
- Ollama  
- MySQL Server  

---

## Setting up your MySQL Server

Make sure to supply valid database credentials in the `application.properties` file.

---

## Current State

The application uses **Ollama** to host a local LLM and make API requests locally.  
Head over to [ollama.com/download](https://ollama.com/download) to download it.

After installing Ollama, run the following command in your terminal:

```bash
ollama run llama3.2
