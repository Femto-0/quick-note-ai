// Select the form and result elements
const noteForm = document.getElementById("noteForm");
const noteContent = document.getElementById("noteContent");
const resultDiv = document.getElementById("result");
const summaryText = document.getElementById("summaryText");
const moodText = document.getElementById("mood");
const clearBtn = document.getElementById("clearBtn");
const notesList = document.getElementById("notesList");
const createNewBtn = document.getElementById("createNewBtn");

// Load previous notes when the page loads
loadPreviousNotes();

// Handle form submission
noteForm.addEventListener("submit", function (event) {
  event.preventDefault(); // Prevent form from refreshing the page

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
    .then((response) => response.json()) // Parse the JSON response
    .then((data) => {
    console.log("Received from backend:", data);
      // Display the summarized note
      summaryText.innerHTML = data.summary || "Summary could not be generated.";
      moodText.innerHTML =
        data.mood || "Unable to determine mood, are you a Robot by any chance?";
      // Refresh the notes list
      loadPreviousNotes();
    })
    .catch((error) => {
      console.error("Error submitting note:", error);
      summaryText.innerHTML = "There was an error processing your note.";
      moodText.innerHTML = "What's your Mood today.....";
    });
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
    .then((response) => response.json())
    .then((notes) => {
      notesList.innerHTML = ""; // Clear existing notes
      notes.forEach((note) => {
        const noteElement = createNoteElement(note);
        notesList.appendChild(noteElement);
      });
    })
    .catch((error) => {
      console.error("Error loading previous notes:", error);
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
