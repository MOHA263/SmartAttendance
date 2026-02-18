package com.attendance.smartattendance.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResetPasswordRequest {
    private String email;
    private String newPassword;

}
