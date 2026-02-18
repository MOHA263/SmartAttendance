package com.attendance.smartattendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One row for the View Attendance popup: Roll No, Name, today's OTP status, and M/T/W/T/F/S.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewAttendanceRowDTO {
    private String rollNumber;
    private String name;
    /** true if student entered OTP today (show green tick), false otherwise (show red X). */
    private boolean presentToday;
    private String mon; // P / A / null
    private String tue;
    private String wed;
    private String thu;
    private String fri;
    private String sat;
}
