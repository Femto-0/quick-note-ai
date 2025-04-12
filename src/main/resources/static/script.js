// Select the form and result elements
const noteForm = document.getElementById('noteForm');
const noteContent = document.getElementById('noteContent');
const resultDiv = document.getElementById('result');
const summaryText = document.getElementById('summaryText');
const mood = document.getElementById('mood'); 
const clearBtn = document.getElementById('clearBtn');

// Handle form submission
noteForm.addEventListener('submit', function (event) {
    event.preventDefault();  // Prevent form from refreshing the page

    const note = {
        content: noteContent.value
    };

    // Send POST request to backend API
    fetch('http://localhost:8080/api/notes', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(note)
    })
    .then(response => response.json())  // Parse the JSON response
    .then(data => {
        // Display the summarized note
        summaryText.innerHTML = data.summary || "Summary could not be generated.";
        mood.innerHTML = data.mood || "Unable to determine mood, are you a Robot by any chance?";
    })
    .catch(error => {
        console.error("Error submitting note:", error);
        summaryText.innerHTML = "There was an error processing your note.";
        mood.innerHTML = "Vibe could not be determined due to an error.";
    });
});

// Handle clear button click
clearBtn.addEventListener('click', function () {
    // Clear the text area
    noteContent.value = '';
    // Reset the summary and mood to the default text
    summaryText.innerHTML = "Your summarized note will appear here...";
    mood.innerHTML = "What's your Vibe today.....";
});
