package com.attendance.smartattendance.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TeacherLoginRequest {

    private String teacherId;
    private String password;

}
