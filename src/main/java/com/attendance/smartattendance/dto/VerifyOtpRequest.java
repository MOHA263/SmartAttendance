package com.attendance.smartattendance.dto;

import lombok.Data;

@Data
public class VerifyOtpRequest {
    private String email;
    private String rollNumber;
    private String otp;

}

