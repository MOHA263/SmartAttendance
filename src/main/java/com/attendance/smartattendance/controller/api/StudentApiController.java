package com.attendance.smartattendance.controller.api;

import com.attendance.smartattendance.dto.SubmitOtpRequest;
import com.attendance.smartattendance.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentApiController {
    
    @Autowired private OtpService otpService;

    // ✅ Student submits OTP (uses StudentOtp table and creates Attendance on success)
    @PostMapping("/submit-otp")
    public ResponseEntity<?> submitOtp(@RequestBody SubmitOtpRequest submitOtpRequest) {
        if (otpService.getNormalOtpRemainingSeconds() <= 0) {
            return ResponseEntity.badRequest()
                    .body("OTP not generated or expired");
        }
        try {
            String result = otpService.verifyOtp(submitOtpRequest.getRollNumber(), submitOtpRequest.getOtp());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/otp-status")
    public boolean otpStatus() {
        // Return true if a normal OTP exists and is still valid
        return otpService.getNormalOtpRemainingSeconds() > 0;
    }

    // 🔁 Student requests OTP again
    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtpAgain(@RequestBody(required = false) java.util.Map<String, String> body) {
        try {
            // Get roll number from request body or session if available
            String rollNumber = (body != null) ? body.get("rollNumber") : null;
            
        if (rollNumber != null && !rollNumber.isEmpty()) {
                otpService.generateRequestOtp(rollNumber);  // ✅ updated method
            return ResponseEntity.ok("OTP request sent. Check your email.");
        } else {
            return ResponseEntity.badRequest().body("Roll number required");
        }

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to request OTP: " + e.getMessage());
        }
    }

}
