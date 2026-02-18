function togglePwd() {
    const pwd = document.getElementById("password");
    if (!pwd) return;

    pwd.type = pwd.type === "password" ? "text" : "password";
}

function registerTeacher() {
    const teacherId = document.getElementById("teacherId").value.trim();
    const username = document.getElementById("username").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;
    const confirm = document.getElementById("confirmPassword").value;
    const msg = document.getElementById("msg");

    // ---------- BASIC VALIDATION ----------
    if (!teacherId || !username || !email || !password || !confirm) {
        showMsg("All fields are required ❌", "red");
        return;
    }

    if (teacherId.length !== 6) {
        showMsg("Teacher ID must be 6 digits ❌", "red");
        return;
    }

    if (!/^\S+@\S+\.\S+$/.test(email)) {
        showMsg("Enter a valid email ❌", "red");
        return;
    }

    if (password.length < 8) {
        showMsg("Password must be at least 8 characters ❌", "red");
        return;
    }

    if (password !== confirm) {
        showMsg("Passwords do not match ❌", "red");
        return;
    }

    // ---------- API CALL ----------
    fetch("/api/teacher/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            teacherId,
            username,
            email,
            password
        })
    })
    .then(res => {
        if (!res.ok) return res.text().then(err => { throw err; });
        return res.text();
    })
    .catch(err => {
        showMsg(err || "Registration failed ❌", "red");
    });

    function showMsg(text, color) {
        msg.innerText = text;
        msg.style.color = color;
    }
}