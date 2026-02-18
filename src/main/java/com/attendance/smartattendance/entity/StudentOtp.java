package com.attendance.smartattendance.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "student_otp")
public class StudentOtp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String otp;
    private String rollNumber;
    private LocalDateTime expiryTime;
    private boolean used;
    @ManyToOne
    @JoinColumn(name="student_id")
    private Student student;
}
