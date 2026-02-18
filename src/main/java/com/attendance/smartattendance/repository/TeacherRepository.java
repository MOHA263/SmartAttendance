package com.attendance.smartattendance.repository;

import com.attendance.smartattendance.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByTeacherId(String teacherId);
    Optional<Teacher> findByEmail(String email);
    Optional<Teacher> findByVerificationToken(String token);
    Optional<Teacher> findByDeleteToken(String token);

    boolean existsByEmail(String email);
    boolean existsById(String teacherId);
}
