package com.attendance.smartattendance.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "students",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "roll_number"),
                @UniqueConstraint(columnNames = "email")
        })
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;


    @Column(name = "roll_number")
    private String rollNumber;

    @Column(nullable = false)
    private String email;

    private Boolean presentToday;

    private String otp;
    private LocalDateTime otpExpiry;

    private LocalDateTime otpGeneratedAt;

    private String classroomCode;
    
}
