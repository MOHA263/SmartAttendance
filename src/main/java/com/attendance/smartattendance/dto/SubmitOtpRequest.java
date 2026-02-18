package com.attendance.smartattendance.dto;

import lombok.Data;

@Data
public class SubmitOtpRequest {

    private String otp;
    private String rollNumber;

}
