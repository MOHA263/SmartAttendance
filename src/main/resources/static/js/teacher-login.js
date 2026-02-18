const card = document.getElementById("authCard");

/* ---------- FLIP NAVIGATION ---------- */

function goRegister(e) {
    e.preventDefault();
    card.classList.add("flip-right");

    setTimeout(() => {
        window.location.href = "/teacher/register";
    }, 600);
}

function goForgot(e) {
    e.preventDefault();
    card.classList.add("flip-left");

    setTimeout(() => {
        window.location.href = "/teacher/forgot-password";
    }, 600);
}

/* ---------- PASSWORD TOGGLE ---------- */

function togglePassword() {
    const pwd = document.getElementById("password");
    if (!pwd) return;

    pwd.type = pwd.type === "password" ? "text" : "password";
}

/* ---------- LOGIN API ---------- */

function loginTeacher() {
    const teacherId = document.getElementById("teacherId").value.trim();
    const password = document.getElementById("password").value.trim();
    const message = document.getElementById("message");

    if (teacherId.length !== 6) {
        message.innerHTML = "<span class='error'>Teacher ID must be 6 digits ❌</span>";
        return;
    }

    fetch("/api/teacher/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ teacherId, password })
    })
    .then(res => {
        if (!res.ok) return res.text().then(t => { throw new Error(t || 'Login failed'); });
        return res.text();
    })
    .then(() => {
        message.innerHTML = "<span class='success'>Login successful ✔</span>";

        setTimeout(() => {
            window.location.href = "/teacher/dashboard";
        }, 800);
    })
    .catch(err => {
        const text = err && err.message ? err.message : 'Invalid credentials ❌';
        message.innerHTML = `<span class='error'>${text}</span>`;
    });
}

/* ---------- EVENT BINDING (SAFE) ---------- */

document.addEventListener("DOMContentLoaded", () => {
    const registerLink = document.querySelector(".flip-right");
    const forgotLink = document.querySelector(".flip-left");

    if (registerLink) registerLink.addEventListener("click", goRegister);
    if (forgotLink) forgotLink.addEventListener("click", goForgot);
});
