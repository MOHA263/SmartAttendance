package com.attendance.smartattendance.service;

import com.attendance.smartattendance.entity.Attendance;
import com.attendance.smartattendance.entity.Student;
import com.attendance.smartattendance.entity.StudentOtp;
import com.attendance.smartattendance.repository.AttendanceRepository;
import com.attendance.smartattendance.repository.StudentOtpRepository;
import com.attendance.smartattendance.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class OtpService {

    @Autowired private StudentRepository studentRepository;
    @Autowired private StudentOtpRepository studentOtpRepository;
    @Autowired private AttendanceRepository attendanceRepository;
    @Autowired private EmailService emailService;
    @Autowired private AttendanceService attendanceService;

    // OTP expiration
    public static final long OTP_VALID_SECONDS = 120; // 2 minutes

    // Separate fields for global normal OTP
    private String normalOtp;
    private LocalDateTime normalOtpTime;

    // Separate fields for student-requested OTP
    private String requestOtp;
    private LocalDateTime requestOtpTime;


    // ==============================
    // NORMAL OTP (SEND TO ALL)
    // ==============================
    public void sendOtpToAllStudents() {
        List<Student> students = studentRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        normalOtpTime = now; // so getNormalOtpRemainingSeconds() works and student page shows timer

        for (Student student : students) {
            String otp = generate6DigitOtp();

            StudentOtp studentOtp = new StudentOtp();
            studentOtp.setStudent(student);
            studentOtp.setOtp(otp);
            studentOtp.setExpiryTime(now.plusSeconds(OTP_VALID_SECONDS));
            studentOtp.setUsed(false);
            System.out.println("OTP:"+otp);
            studentOtpRepository.save(studentOtp);

            student.setOtpGeneratedAt(now); // so teacher can manually update attendance after 5 min
            studentRepository.save(student);

            emailService.sendOtp(student.getEmail(), otp);
        }
    }

    public boolean validateNormalOtp(String otp) {
        if (normalOtp == null) return false;

        long seconds = Duration.between(normalOtpTime, LocalDateTime.now()).getSeconds();
        return normalOtp.equals(otp) && seconds <= OTP_VALID_SECONDS;
    }


    // ==============================
    // REQUEST OTP (STUDENT-SPECIFIC)
    // ==============================
    public void generateRequestOtp(String rollNumber) {
        Student student = studentRepository.findByRollNumber(rollNumber)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        requestOtp = generate6DigitOtp();
        requestOtpTime = LocalDateTime.now();

        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByStudentAndDate(student, today)
                .orElse(new Attendance());

        attendance.setOtp(requestOtp);
        attendance.setOtpUsed(false);
        attendance.setOtpExpiry(requestOtpTime.plusSeconds(OTP_VALID_SECONDS));
        attendance.setStudent(student);
        attendance.setDate(today);
        attendance.setRollNumber(student.getRollNumber());

        attendanceRepository.save(attendance);

        emailService.sendMail(
                student.getEmail(),
                "Request Attendance OTP",
                "Your requested OTP is: " + requestOtp
        );
    }

    public boolean validateRequestOtp(String otp) {
        if (requestOtp == null) return false;

        long seconds = Duration.between(requestOtpTime, LocalDateTime.now()).getSeconds();
        return requestOtp.equals(otp) && seconds <= OTP_VALID_SECONDS;
    }


    // ==============================
    // VERIFY OTP FROM STUDENT
    // ==============================
    public String verifyOtp(String rollNo, String otp) {
        Student student = studentRepository.findByRollNumber(rollNo)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        StudentOtp studentOtp = studentOtpRepository
                .findTopByStudentAndUsedFalseOrderByExpiryTimeDesc(student)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (studentOtp.isUsed()) return "OTP already used";

        if (LocalDateTime.now().isAfter(studentOtp.getExpiryTime())) return "OTP expired";

        if (!studentOtp.getOtp().equals(otp)) return "Invalid OTP";

        studentOtp.setUsed(true);
        studentOtpRepository.save(studentOtp);

        // Create/update Attendance so teacher dashboard shows present today
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByStudentAndDate(student, today)
                .orElse(new Attendance());
        attendance.setStudent(student);
        attendance.setDate(today);
        attendance.setPresent(true);
        attendance.setRollNumber(student.getRollNumber());
        attendanceRepository.save(attendance);

        attendanceService.updateWeeklyAttendance(student, today, true);

        return "Attendance marked successfully";
    }


    // ==============================
    // HELPER METHODS
    // ==============================
    private String generate6DigitOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    public long getNormalOtpRemainingSeconds() {
        if (normalOtpTime == null) return 0;
        return OTP_VALID_SECONDS - Duration.between(normalOtpTime, LocalDateTime.now()).getSeconds();
    }

    public long getRequestOtpRemainingSeconds() {
        if (requestOtpTime == null) return 0;
        return OTP_VALID_SECONDS - Duration.between(requestOtpTime, LocalDateTime.now()).getSeconds();
    }


    // ==============================
    // SEND OTP AND AUTO-MARK ATTENDANCE
    // ==============================
    public void sendOtpAndMark() {
        sendOtpToAllStudents();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                studentRepository.findAll().forEach(student -> {
                    LocalDate today = LocalDate.now();
                    boolean presentToday = attendanceRepository.findByStudentAndDate(student, today)
                            .map(Attendance::getPresent)
                            .orElse(false);

                    if (presentToday) {
                        attendanceService.updateWeeklyAttendance(student, today, true);
                    }
                });
            }
        }, OTP_VALID_SECONDS * 1000);
    }
}
