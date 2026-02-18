document.addEventListener("DOMContentLoaded", () => {
  resetTodayColumnIfNewDay();
  loadStudents();
});

/* ==============================
   LOAD STUDENTS (Today = - / P / A)
================================ */
function loadStudents() {
  fetch("/api/teacher/all-students")
    .then(res => res.json())
    .then(students => {
      const tbody = document.getElementById("studentTableBody");
      tbody.innerHTML = "";

      students.forEach(s => {

        let todayValue = "null";
        if (s.presentToday === true) {
          todayValue = `<span class="status present">P</span>`;
        } else if (s.presentToday === false) {
          todayValue = `<span class="status absent">A</span>`;
        } else {
          todayValue = `<span class="today-empty">-</span>`;
        }

        tbody.innerHTML += `
          <tr>
            <td>${s.id}</td>
            <td>${s.name}</td>
            <td>${s.rollNumber}</td>
            <td>${s.email}</td>
            <td class="today-cell">${todayValue}</td>
            <td class="actions">
              <i class="fa fa-pen edit" onclick="editStudent(${s.id})"></i>
              <i class="fa fa-trash delete" onclick="deleteStudent(${s.id})"></i>
            </td>
          </tr>
        `;
      });
    });
}

/* ==============================
   ADD STUDENT
================================ */
function openAddStudentModal() {
  studentModal.style.display = "flex";
}

function closeModal() {
  studentModal.style.display = "none";
}

function saveStudent() {

  const name = document.getElementById("Name").value.trim();
  const rollNumber = document.getElementById("rollNumber").value.trim();
  const email = document.getElementById("email").value.trim();


  fetch("/api/teacher/add-student", {
    method: "POST",
    headers: {"Content-Type": "application/json"},
    body: JSON.stringify({ name, rollNumber, email })

  })

  .then(res => res.text())
  .then(msg => {
    alert(msg);
    closeModal();
    loadStudents();
})

}

/* ==============================
   EDIT STUDENT + MANUAL ATTENDANCE
================================ */
const editName = document.getElementById("editName");
const editEmail = document.getElementById("editEmail");
const editAttendance = document.getElementById("editAttendance");
const editModal = document.getElementById("editModal");

let editStudentId;

function editStudent(id) {
    fetch("/api/teacher/all-students")
      .then(res => res.json())
      .then(list => {
        const s = list.find(x => x.id === id);
        editStudentId = id;

        editName.value = s.name;
        editEmail.value = s.email;

        editAttendance.style.display = "none";
            editAttendance.value = "P"; // default value

            editModal.style.display = "flex";

            // show dropdown after 5 minutes
            setTimeout(() => {
                editAttendance.style.display = "block";
            }, 5 * 60 * 1000); // 5 min delay
        })
    
    .catch(err => console.error("Failed to fetch student:", err));
}

function closeEditModal() {
    editModal.style.display = "none";
}

function updateStudent() {
    const attendance = editAttendance.style.display !== "none" ? editAttendance.value : null;

    fetch(`/api/teacher/update-student-attendance/${editStudentId}`, {
        method: "PUT",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({
            name: editName.value,
            email: editEmail.value,
            attendance: attendance.value
        })
    })
    .then(res => res.text())
    .then(msg => {
        alert(msg);
        closeEditModal();
        loadStudents();
    })
    .catch(_err => alert("Failed to update student ❌"));
}


/* ==============================
   DELETE STUDENT
================================ */
function deleteStudent(id) {
  if (!confirm("Delete student?")) return;

  fetch(`/api/teacher/${id}`, { method: "DELETE" })
    .then(res => res.text())
    .then(alert)
    .then(loadStudents);
}

/* ==============================
   OTP LOGIC
   - Send OTP
   - After 2 minutes update attendance
================================ */
function sendOtp() {
  fetch("/api/teacher/send-otp", { method: "POST" })
    .then(res => res.json())
    .then(_data => {
      alert("✅ OTP sent to all students. Valid for 2 minutes.\n\nAttendance will be updated in 2 minutes...");
      
      // Wait 2 minutes before refreshing student list to show attendance
      setTimeout(() => {
        loadStudents(); // Now display P/A status after OTP window closes
        alert("📊 Attendance updated! Check the 'Today' column.");
      }, 2 * 60 * 1000); // 2 minutes = 120 seconds
    })
    .catch(err => {
      console.error("Failed to send OTP:", err);
      alert("❌ Failed to send OTP. Please try again.");
    });
}

/* ==============================
   WEEKLY ATTENDANCE POPUP (M T W T F S)
================================ */
function openWeeklyAttendancePopup() {
    const attendanceTableBody = document.getElementById("attendanceTableBody");
    const attendancePopup = document.getElementById("attendancePopup");

    attendancePopup.style.display = "flex";
    attendanceTableBody.innerHTML = `
      <tr><td colspan="9" style="text-align: center; padding: 20px;">Loading...</td></tr>
    `;

    fetch("/api/attendance/weekly")
        .then(res => res.json())
        .then(list => {
            attendanceTableBody.innerHTML = "";

            if (list.length === 0) {
                attendanceTableBody.innerHTML = `
                  <tr>
                    <td colspan="9" style="text-align: center; padding: 20px; font-size: 18px; color: #666;">
                      No weekly attendance yet. Mark attendance (Send OTP / student submit) to see the report.
                    </td>
                  </tr>
                `;
            } else {
                list.forEach(row => {
                    const attendanceCell = row.presentToday
                        ? '<span class="attendance-tick" title="Entered OTP"><i class="fa fa-check-circle"></i></span>'
                        : '<span class="attendance-cross" title="Did not enter OTP"><i class="fa fa-times-circle"></i></span>';
                    attendanceTableBody.innerHTML += `
                      <tr>
                        <td>${row.rollNumber}</td>
                        <td>${row.name}</td>
                        <td class="attendance-cell">${attendanceCell}</td>
                        <td class="${weekClass(row.mon)}">${row.mon ?? "-"}</td>
                        <td class="${weekClass(row.tue)}">${row.tue ?? "-"}</td>
                        <td class="${weekClass(row.wed)}">${row.wed ?? "-"}</td>
                        <td class="${weekClass(row.thu)}">${row.thu ?? "-"}</td>
                        <td class="${weekClass(row.fri)}">${row.fri ?? "-"}</td>
                        <td class="${weekClass(row.sat)}">${row.sat ?? "-"}</td>
                      </tr>
                    `;
                });
            }

            attendancePopup.style.display = "flex";
        })
        .catch(err => console.error("Failed to fetch weekly attendance:", err));
      }

function closeAttendancePopup() {
    document.getElementById("attendancePopup").style.display = "none";
}

function weekClass(value) {
    if (value === "P") return "present";
    if (value === "A") return "absent";
    return "";
}


/* ==============================
   RESET TODAY COLUMN EVERY DAY
================================ */
function resetTodayColumnIfNewDay() {
  const today = new Date().toDateString();
  const last = localStorage.getItem("lastAttendanceDate");

  if (last !== today) {
    fetch("/api/attendance/reset-today", { method: "POST" });
    localStorage.setItem("lastAttendanceDate", today);
  }
}

/* ==============================
   ACCOUNT
================================ */
function logout() {
    fetch('/api/teacher/logout', { method: 'POST' })
      .then(() => window.location.href = '/teacher/login');
}


function deleteAccount() {
  if (!confirm("Delete account permanently?")) return;

  const email = prompt("Enter your email to confirm:");
  if (!email) return;

  fetch("/api/teacher/request-delete", {
    method: "POST",
    headers: {"Content-Type": "application/json"},
    body: JSON.stringify({ email })
  })
  .then(res => res.text())
  .then(alert);
}
