package com.attendance.smartattendance.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudentPageController {

    @GetMapping("/role")
    public String rolePage() {
        return "role";
    }

    @GetMapping("/student")
    public String studentPage() {
        return "student-page";
    }

}
