const card = document.getElementById("roleCard");

function goTeacher() {
    if (card) {
        card.classList.add("flip");
        setTimeout(() => {
            window.location.href = "/teacher/login";
        }, 400);
    } else {
        // Fallback if card element is not found
        window.location.href = "/teacher/login";
    }
}

function goStudent() {
    if (card) {
        card.classList.add("flip");
        setTimeout(() => {
            window.location.href = "/student";
        }, 400);
    } else {
        // Fallback if card element is not found
        window.location.href = "/student";
    }
}
