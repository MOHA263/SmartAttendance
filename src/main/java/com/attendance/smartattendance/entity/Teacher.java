package com.attendance.smartattendance.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "teachers")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @Column(unique = true)
    private String teacherId; // 6-digit ID

    private String password;

    private String resetOtp;
    private LocalDateTime otpExpiry;

    private String classroomCode;  // classroom separation

    // verification for registration
    private String verificationToken;
    private boolean verified = false;

    // deletion verification
    private String deleteToken;
    private LocalDateTime deleteTokenExpiry;

}
