package com.attendance.smartattendance.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    private LocalDate date;

    private Boolean present;

    private String rollNumber;

    private String otp;

    private boolean otpUsed;

    private LocalDateTime otpExpiry;

}
