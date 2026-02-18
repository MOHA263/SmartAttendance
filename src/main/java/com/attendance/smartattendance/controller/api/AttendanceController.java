package com.attendance.smartattendance.controller.api;

import com.attendance.smartattendance.dto.SubmitOtpRequest;
import com.attendance.smartattendance.dto.ViewAttendanceRowDTO;
import com.attendance.smartattendance.entity.Attendance;
import com.attendance.smartattendance.entity.Student;
import com.attendance.smartattendance.entity.WeeklyAttendance;
import com.attendance.smartattendance.repository.AttendanceRepository;
import com.attendance.smartattendance.repository.StudentRepository;
import com.attendance.smartattendance.repository.TeacherRepository;
import com.attendance.smartattendance.repository.WeeklyAttendanceRepository;
import com.attendance.smartattendance.service.AttendanceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;

    public AttendanceController(StudentRepository studentRepository,
                                AttendanceRepository attendanceRepository) {
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Autowired private WeeklyAttendanceRepository weeklyAttendanceRepository;
    @Autowired private TeacherRepository teacherRepository;
    @Autowired private AttendanceService attendanceService;

    /** Weekly attendance for View button – Roll No, Name, Attendance (today OTP), M/T/W/T/F/S. */
    @GetMapping("/weekly")
    public List<ViewAttendanceRowDTO> weekly(HttpSession session) {
        List<WeeklyAttendance> list;
        String teacherId = (String) session.getAttribute("teacherId");
        if (teacherId != null) {
            list = teacherRepository.findByTeacherId(teacherId)
                    .map(teacher -> {
                        List<String> rollNumbers = studentRepository
                                .findByClassroomCode(teacher.getClassroomCode())
                                .stream()
                                .map(Student::getRollNumber)
                                .collect(Collectors.toList());
                        if (rollNumbers.isEmpty()) return List.<WeeklyAttendance>of();
                        return weeklyAttendanceRepository.findByRollNumberIn(rollNumbers);
                    })
                    .orElse(List.of());
        } else {
            list = weeklyAttendanceRepository.findAll();
        }

        LocalDate today = LocalDate.now();
        Set<String> presentTodayRollNumbers = attendanceRepository.findByDate(today).stream()
                .filter(a -> Boolean.TRUE.equals(a.getPresent()) && a.getStudent() != null)
                .map(a -> a.getStudent().getRollNumber())
                .collect(Collectors.toSet());

        return list.stream()
                .map(wa -> new ViewAttendanceRowDTO(
                        wa.getRollNumber(),
                        wa.getName(),
                        presentTodayRollNumbers.contains(wa.getRollNumber()),
                        wa.getMon(),
                        wa.getTue(),
                        wa.getWed(),
                        wa.getThu(),
                        wa.getFri(),
                        wa.getSat()
                ))
                .collect(Collectors.toList());
    }

    

      @PostMapping("/submit-otp")
        public ResponseEntity<String> submitOtp(@RequestBody SubmitOtpRequest request) {

        Student student = studentRepository
                .findByRollNumber(request.getRollNumber())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (student.getOtp() == null)
            return ResponseEntity.badRequest().body("OTP not generated");

        if (!student.getOtp().equals(request.getOtp()))
            return ResponseEntity.badRequest().body("Invalid OTP");

        if (student.getOtpExpiry().isBefore(LocalDateTime.now()))
            return ResponseEntity.badRequest().body("OTP expired");

        LocalDate today = LocalDate.now();

        if (attendanceRepository.existsByStudentAndDate(student, today))
            return ResponseEntity.ok("Attendance already marked");

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setDate(today);
        attendance.setPresent(true);

        attendanceRepository.save(attendance);

        // clear OTP
        student.setOtp(null);
        student.setOtpExpiry(null);
        studentRepository.save(student);

        return ResponseEntity.ok("Attendance marked successfully");
    }


    // ===== RESET TODAY ATTENDANCE =====
    @PostMapping("/reset-today")
    public ResponseEntity<?> resetTodayAttendance() {
        attendanceService.resetDailyAttendance();
        return ResponseEntity.ok("Daily attendance reset successfully");
    }

}
