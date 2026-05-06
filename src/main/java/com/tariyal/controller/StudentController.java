package com.tariyal.controller;


import com.tariyal.dto.StudentDropDown;
import com.tariyal.dto.StudentRequest;
import com.tariyal.dto.StudentResponse;
import com.tariyal.entity.Student;
import com.tariyal.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;


    @PostMapping("/add")
    public ResponseEntity<?> addStudent(@RequestBody StudentRequest request,
                                        Authentication authentication) {

        // 🔥 email comes from JWT (Spring Security context)
        String email = authentication.getName();
        Student saved = studentService.addStudent(request, email);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getStudentDropdown(Authentication authentication) {
        String email = authentication.getName();
        List<StudentDropDown> students = studentService.getAllStudents(email);
        return ResponseEntity.ok(students);
    }
}
