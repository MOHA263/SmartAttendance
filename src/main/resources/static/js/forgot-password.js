const msg = document.getElementById("msg");
const resendBtn = document.getElementById("resendBtn");
const timerText = document.getElementById("timerText");

let timer;
let timeLeft = 120;

// ---------------- SEND OTP ----------------
function sendOtp() {
    const email = document.getElementById("email").value.trim();

    if (!email) {
        showMsg("Email is required ❌", "red");
        return;
    }

    fetch("/api/teacher/forgot-password", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email })
    })
    .then(res => {
        if (!res.ok) return res.text().then(err => { throw err; });
        return res.text();
    })
    .then(() => {
        showMsg("OTP sent to email ✔", "green");
        document.getElementById("otpBox").classList.remove("hidden");
        startTimer();
    })
    .catch(err => showMsg(err, "red"));
}

// ---------------- VERIFY OTP ----------------
function verifyOtp() {
    const email = document.getElementById("email").value.trim();
    const otp = document.getElementById("otp").value.trim();

    if (otp.length !== 6) {
        showMsg("OTP must be 6 digits ❌", "red");
        return;
    }

    fetch("/api/teacher/verify-otp", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, otp })
    })
    .then(res => {
        if (!res.ok) return res.text().then(err => { throw err; });
        return res.text();
    })
    .then(() => {
        clearInterval(timer); // ⛔ STOP TIMER
        timerText.innerText = "";

        showMsg("OTP verified ✔", "green");
        document.getElementById("otpBox").classList.add("hidden");
        document.getElementById("passwordBox").classList.remove("hidden");
    })
    .catch(err => showMsg(err, "red"));
}

// ---------------- RESET PASSWORD ----------------
function resetPassword() {
    const email = document.getElementById("email").value.trim();
    const newPassword = document.getElementById("newPassword").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    if (!newPassword || !confirmPassword) {
        showMsg("Fill all fields ❌", "red");
        return;
    }

    if (newPassword.length < 8) {
        showMsg("Password must be 8 characters ❌", "red");
        return;
    }

    if (newPassword !== confirmPassword) {
        showMsg("Passwords do not match ❌", "red");
        return;
    }

    fetch("/api/teacher/reset-password", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, newPassword })
    })
    .then(res => res.text())
    .then(() => {
        alert("Password updated successfully ✔");
        window.location.href = "/teacher/login";
    })
    .catch(() => alert("Password reset failed ❌"));
}

// ---------------- TIMER ----------------
function startTimer() {
    timeLeft = 120;
    resendBtn.disabled = true;

    timer = setInterval(() => {
        const min = Math.floor(timeLeft / 60);
        const sec = timeLeft % 60;
        timerText.innerText = `Resend OTP in ${min}:${sec < 10 ? "0" : ""}${sec}`;
        timeLeft--;

        if (timeLeft < 0) {
            clearInterval(timer);
            resendBtn.disabled = false;
            timerText.innerText = "You can resend OTP now";
        }
    }, 1000);
}

function showMsg(text, color) {
    msg.innerText = text;
    msg.style.color = color;
}
