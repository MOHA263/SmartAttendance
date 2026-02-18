package com.attendance.smartattendance.repository;

import com.attendance.smartattendance.entity.Student;
import com.attendance.smartattendance.entity.StudentOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StudentOtpRepository extends JpaRepository<StudentOtp, Long> {

    Optional<StudentOtp> findByStudentAndOtpAndUsedFalse(Student student, String otp);

    List<StudentOtp> findByUsedFalseAndExpiryTimeBefore(LocalDateTime time);

    Optional<StudentOtp> findTopByStudentAndUsedFalseOrderByExpiryTimeDesc(Student student);
}
