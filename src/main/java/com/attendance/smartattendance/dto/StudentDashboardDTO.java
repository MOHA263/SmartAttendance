package com.attendance.smartattendance.dto;

import lombok.Data;

@Data
public class StudentDashboardDTO {
    private Long id;
    private String name;
    private String rollNumber;
    private String email;

    private Boolean presentToday; // null = not started
    private boolean otpSent;
}
