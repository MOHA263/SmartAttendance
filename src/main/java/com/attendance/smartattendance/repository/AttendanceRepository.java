package com.attendance.smartattendance.repository;

import com.attendance.smartattendance.entity.Attendance;
import com.attendance.smartattendance.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

        Optional<Attendance> findByStudentAndDate(Student student, LocalDate date);

        Optional<Attendance> findByRollNumberAndOtpAndDate(
                String rollNumber,
                String otp,
                LocalDate date
        );

        List<Attendance> findByStudentOrderByDateDesc(Student student);

        void deleteByStudent(Student student);

        boolean existsByStudentAndDate(Student student, LocalDate date);

        List<Attendance> findByDate(LocalDate date);
}
