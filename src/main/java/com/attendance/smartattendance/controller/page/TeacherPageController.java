package com.attendance.smartattendance.controller.page;

import com.attendance.smartattendance.service.OtpService;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TeacherPageController {

    @Autowired OtpService otpService;

    @GetMapping("/teacher/login")
    public String teacherLogin() {
        return "teacher-login";
    }

    @GetMapping("/teacher/dashboard")
    public String dashboard(HttpSession session) {
        if (session.getAttribute("teacherId") == null) {
        return "redirect:/teacher/login";
        }
        return "teacher-dashboard";
    }

    @GetMapping("/teacher/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
    session.invalidate(); // clears the session
    return ResponseEntity.ok(Map.of("status","success","message","Logged out successfully"));
    }


    @PostMapping("/teacher/send-otp")
    public ResponseEntity<?> sendOtp() {
        otpService.sendOtpToAllStudents();
        return ResponseEntity.ok("OTP sent to all students");
    }

    @GetMapping("/teacher/register")
    public String registerPage() {
        return "register";
    }

}
