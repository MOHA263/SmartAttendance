package com.attendance.smartattendance.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class WeeklyAttendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rollNumber;
    private String name;

    private String mon; // P / A
    private String tue;
    private String wed;
    private String thu;
    private String fri;
    private String sat;

}
