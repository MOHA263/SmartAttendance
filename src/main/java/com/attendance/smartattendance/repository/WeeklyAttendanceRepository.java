package com.attendance.smartattendance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.attendance.smartattendance.entity.WeeklyAttendance;

public interface WeeklyAttendanceRepository extends JpaRepository<WeeklyAttendance, Long> {
    Optional<WeeklyAttendance> findByRollNumber(String rollNumber);
    List<WeeklyAttendance> findByRollNumberIn(List<String> rollNumbers);
}
