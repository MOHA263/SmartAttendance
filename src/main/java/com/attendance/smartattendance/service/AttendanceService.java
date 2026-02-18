package com.attendance.smartattendance.service;

import com.attendance.smartattendance.entity.Attendance;
import com.attendance.smartattendance.entity.Student;
import com.attendance.smartattendance.entity.WeeklyAttendance;
import com.attendance.smartattendance.repository.AttendanceRepository;
import com.attendance.smartattendance.repository.StudentRepository;
import com.attendance.smartattendance.repository.WeeklyAttendanceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendanceService {

    @Autowired private
    AttendanceRepository attendanceRepository;
    @Autowired private
    StudentRepository studentRepository;
    @Autowired private WeeklyAttendanceRepository weeklyAttendanceRepository;


    // STUDENT submits OTP
    public String markAttendance(String rollNumber, String otp) {

        Student student = studentRepository.findByRollNumber(rollNumber)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Attendance attendance = attendanceRepository
                .findByStudentAndDate(student, LocalDate.now())
                .orElseThrow(() -> new RuntimeException("OTP not generated"));

        if (!attendance.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }


        if (attendanceRepository.existsByStudentAndDate(student, LocalDate.now())) {
            throw new RuntimeException("Attendance already marked");
        }

        if (attendance.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        if (attendance.getPresent()) {
            throw new RuntimeException("Attendance already marked");
        }

        attendance.setStudent(student);
        attendance.setDate(LocalDate.now());
        attendance.setPresent(true);
        attendanceRepository.save(attendance);

        return "ATTENDANCE MARKED";
    }

    // TEACHER dashboard
    public boolean isStudentPresentToday(Student student) {
        return attendanceRepository
                .findByStudentAndDate(student, LocalDate.now())
                .map(Attendance::getPresent)
                .orElse(false);
    }


    public void updateWeekly(Student s, String status) {

        WeeklyAttendance wa = weeklyAttendanceRepository
                .findByRollNumber(s.getRollNumber())
                .orElseGet(() -> {
                    WeeklyAttendance w = new WeeklyAttendance();
                    w.setRollNumber(s.getRollNumber());
                    w.setName(s.getName());
                    return w;
                });

        DayOfWeek day = LocalDate.now().getDayOfWeek();

        switch (day) {
            case MONDAY -> wa.setMon(status);
            case TUESDAY -> wa.setTue(status);
            case WEDNESDAY -> wa.setWed(status);
            case THURSDAY -> wa.setThu(status);
            case FRIDAY -> wa.setFri(status);
            case SATURDAY -> wa.setSat(status);
        }

        weeklyAttendanceRepository.save(wa);
    }

    // ===== SEND OTP =====
    public String sendOtp(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Attendance attendance = attendanceRepository.findByStudentAndDate(student, LocalDate.now())
                .orElse(new Attendance());

        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
        attendance.setStudent(student);
        attendance.setDate(LocalDate.now());
        attendance.setOtp(otp);
        attendance.setOtpExpiry(LocalDateTime.now().plusMinutes(2));
        attendance.setOtpUsed(false);
        attendance.setRollNumber(student.getRollNumber());
        attendanceRepository.save(attendance);

        return otp; // send via email/SMS in controller
    }

    // ===== VERIFY OTP =====
    public void verifyOtp(Long studentId, String submittedOtp) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Attendance attendance = attendanceRepository.findByStudentAndDate(student, LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Attendance not found"));

        if (!submittedOtp.equals(attendance.getOtp()) ||
                attendance.isOtpUsed() ||
                attendance.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        attendance.setOtpUsed(true);
        attendance.setPresent(true); // mark P
        attendanceRepository.save(attendance);

        updateWeeklyAttendance(student, LocalDate.now(), true);
    }

    // ===== MANUAL UPDATE BY TEACHER (5-minute delay enforced) =====
    public void updateAttendance(Long studentId, String attendanceValue) {
        boolean present = "P".equals(attendanceValue);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Attendance attendance = attendanceRepository.findByStudentAndDate(student, LocalDate.now())
                .orElse(new Attendance());

        attendance.setStudent(student);
        attendance.setDate(LocalDate.now());
        attendance.setPresent(present);
        attendance.setRollNumber(student.getRollNumber());
        attendanceRepository.save(attendance);

        updateWeeklyAttendance(student, LocalDate.now(), present);
    }

    // ===== UPDATE WEEKLY ATTENDANCE =====
    public void updateWeeklyAttendance(Student student, LocalDate date, boolean present) {
        WeeklyAttendance weekly = weeklyAttendanceRepository.findByRollNumber(student.getRollNumber())
                .orElse(new WeeklyAttendance());

        weekly.setRollNumber(student.getRollNumber());
        weekly.setName(student.getName());

        DayOfWeek day = date.getDayOfWeek();
        switch (day) {
            case MONDAY -> weekly.setMon(present ? "P" : "A");
            case TUESDAY -> weekly.setTue(present ? "P" : "A");
            case WEDNESDAY -> weekly.setWed(present ? "P" : "A");
            case THURSDAY -> weekly.setThu(present ? "P" : "A");
            case FRIDAY -> weekly.setFri(present ? "P" : "A");
            case SATURDAY -> weekly.setSat(present ? "P" : "A");
            default -> {}
        }

        weeklyAttendanceRepository.save(weekly);
    }

    // ===== CHECK IF MANUAL UPDATE IS ALLOWED (5-minute delay) =====
    public boolean canManuallyUpdateAttendance(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // If OTP was never generated, manual update is not allowed yet
        if (student.getOtpGeneratedAt() == null) {
            return false;
        }

        // Check if 5 minutes have passed since OTP was generated
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        return student.getOtpGeneratedAt().isBefore(fiveMinutesAgo);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void resetDailyAttendance() {
        // Delete all attendance records for today
        // This ensures the daily column resets every day
        List<Attendance> todayAttendance = attendanceRepository.findByDate(LocalDate.now());
        attendanceRepository.deleteAll(todayAttendance);
    }

    // Reset WeeklyAttendance every Monday at midnight (start of new week)
    @Scheduled(cron = "0 0 0 ? * MON")
    public void resetWeeklyAttendance() {
        List<WeeklyAttendance> allWeekly = weeklyAttendanceRepository.findAll();
        for (WeeklyAttendance wa : allWeekly) {
            wa.setMon(null);
            wa.setTue(null);
            wa.setWed(null);
            wa.setThu(null);
            wa.setFri(null);
            wa.setSat(null);
            weeklyAttendanceRepository.save(wa);
        }
    }
}
