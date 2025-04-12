// Select the form and result elements
const noteForm = document.getElementById("noteForm");
const noteContent = document.getElementById("noteContent");
const resultDiv = document.getElementById("result");
const summaryText = document.getElementById("summaryText");
const moodText = document.getElementById("mood");
const clearBtn = document.getElementById("clearBtn");
const notesList = document.getElementById("notesList");
const createNewBtn = document.getElementById("createNewBtn");

// Create text-to-speech button and status elements
const playBtn = document.createElement("button");
playBtn.id = "playBtn";
playBtn.textContent = "Read Aloud";
playBtn.type = "button";
playBtn.className = "play-btn";
document.querySelector(".button-group").appendChild(playBtn);

// Create status element
const playbackStatus = document.createElement("p");
playbackStatus.id = "playbackStatus";
playbackStatus.textContent = "Ready to read text";
playbackStatus.className = "playback-status";
document.querySelector(".button-group").appendChild(playbackStatus);

// Load previous notes when the page loads
loadPreviousNotes();

// Handle form submission
noteForm.addEventListener("submit", function (event) {
  event.preventDefault(); // Prevent form from refreshing the page

  if (!noteContent.value.trim()) {
    summaryText.innerHTML = "Please write a note first.";
    moodText.innerHTML = "What's your Mood today.....";
    return;
  }

  const note = {
    content: noteContent.value,
  };

  // Send POST request to backend API
  fetch("http://localhost:8080/api/notes", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(note),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return response.json();
    })
    .then((data) => {
      if (!data) {
        throw new Error("No data received from server");
      }
      // Display the summarized note
      summaryText.innerHTML = data.summary || "Summary could not be generated.";
      moodText.innerHTML =
        data.mood || "Unable to determine mood, are you a Robot by any chance?";
      // Refresh the notes list
      loadPreviousNotes();
    })
    .catch((error) => {
      console.error("Error submitting note:", error);
      summaryText.innerHTML =
        "There was an error processing your note. Please try again.";
      moodText.innerHTML = "Unable to determine mood at this time.";
    });
});

// Add speech recognition functionality
document.addEventListener("DOMContentLoaded", function () {
  // Add new event listener for the record button
  const recordBtn = document.getElementById("recordBtn");
  const transcribedText = document.getElementById("transcribedText");

  let recognition;
  try {
    const SpeechRecognition =
      window.SpeechRecognition || window.webkitSpeechRecognition;
    recognition = new SpeechRecognition();
  } catch (e) {
    console.error("Speech recognition not supported", e);
    recordBtn.disabled = true;
    recordBtn.textContent = "Speech Not Supported";
    return;
  }

  recognition.continuous = true;
  recognition.interimResults = true;
  recognition.lang = "en-US";

  let isRecording = false;

  recordBtn.addEventListener("click", function () {
    if (!isRecording) {
      recognition.start();
      recordBtn.textContent = "Stop Recording";
      transcribedText.textContent = "Listening...";
      isRecording = true;
    } else {
      recognition.stop();
      recordBtn.textContent = "Record Audio";
      isRecording = false;
    }
  });

  recognition.onresult = function (event) {
    let interimTranscript = "";
    let finalTranscript = "";

    for (let i = event.resultIndex; i < event.results.length; i++) {
      const transcript = event.results[i][0].transcript;
      if (event.results[i].isFinal) {
        finalTranscript += transcript + " ";
      } else {
        interimTranscript += transcript;
      }
    }

    if (finalTranscript) {
      // Update both the transcribed text and the note content
      transcribedText.textContent = finalTranscript;
      document.getElementById("noteContent").value += finalTranscript;
    } else {
      transcribedText.textContent = interimTranscript;
    }
  };

  recognition.onerror = function (event) {
    console.error("Speech recognition error", event.error);
    transcribedText.textContent = "Error: " + event.error;
    isRecording = false;
    recordBtn.textContent = "Record Audio";
  };

  recognition.onend = function () {
    if (isRecording) {
      // If we're still supposed to be recording, restart the recognition
      recognition.start();
    }
  };
});

// Handle clear button click
clearBtn.addEventListener("click", function () {
  clearForm();
});

// Handle create new button click
createNewBtn.addEventListener("click", function () {
  clearForm();
});

// Function to clear the form
function clearForm() {
  noteContent.value = "";
  summaryText.innerHTML = "Your summarized note will appear here...";
  moodText.innerHTML = "What's your Mood today.....";
}

// Function to load previous notes
function loadPreviousNotes() {
  fetch("http://localhost:8080/api/notes", {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return response.json();
    })
    .then((notes) => {
      if (!Array.isArray(notes)) {
        throw new Error("Invalid response format");
      }
      notesList.innerHTML = ""; // Clear existing notes
      notes.forEach((note) => {
        const noteElement = createNoteElement(note);
        notesList.appendChild(noteElement);
      });
    })
    .catch((error) => {
      console.error("Error loading previous notes:", error);
      notesList.innerHTML =
        '<div class="note-item error">Unable to load previous notes</div>';
    });
}

// Function to create a note element
function createNoteElement(note) {
  const div = document.createElement("div");
  div.className = "note-item";

  const timestamp = new Date(note.timestamp).toLocaleString();
  const content =
    note.content.length > 50
      ? note.content.substring(0, 50) + "..."
      : note.content;

  div.innerHTML = `
        <div class="timestamp">${timestamp}</div>
        <div class="preview">${content}</div>
    `;

  div.addEventListener("click", () => {
    noteContent.value = note.content;
    summaryText.innerHTML = note.summary || "Summary not available";
    moodText.innerHTML = note.mood || "Mood not available";
  });

  return div;
}

// Text-to-speech functionality
playBtn.addEventListener("click", function () {
  // Get text from note content or transcribed text
  const textToRead =
    noteContent.value ||
    (transcribedText &&
    transcribedText.textContent !== "Click 'Record Audio' to start speaking..."
      ? transcribedText.textContent
      : null);

  if (!textToRead) {
    playbackStatus.textContent = "No text available to read";
    return;
  }

  if ("speechSynthesis" in window) {
    window.speechSynthesis.cancel(); // Stop any current speech

    const utterance = new SpeechSynthesisUtterance(textToRead);
    utterance.lang = "en-US";
    utterance.rate = 1.0;

    utterance.onstart = function () {
      playbackStatus.textContent = "Reading...";
      playBtn.disabled = true;
    };

    utterance.onend = function () {
      playbackStatus.textContent = "Playback finished";
      playBtn.disabled = false;
    };

    utterance.onerror = function (event) {
      playbackStatus.textContent = "Error during playback";
      playBtn.disabled = false;
    };

    speechSynthesis.speak(utterance);
  } else {
    playbackStatus.textContent = "Text-to-speech not supported";
    playBtn.disabled = true;
  }
});
