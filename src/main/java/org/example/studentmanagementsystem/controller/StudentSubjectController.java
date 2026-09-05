package org.example.studentmanagementsystem.controller;

import jakarta.validation.Valid;
import org.example.studentmanagementsystem.domain.AssignSubjectResponseDto;
import org.example.studentmanagementsystem.domain.AssignSubjectRequestDto;
import org.example.studentmanagementsystem.service.StudentSubjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students/{studentId}/subjects")
public class StudentSubjectController {
    private final StudentSubjectService studentSubjectService;

    public StudentSubjectController(StudentSubjectService studentSubjectService) {
        this.studentSubjectService = studentSubjectService;
    }

    @PostMapping
    public ResponseEntity<AssignSubjectResponseDto> assignSubject(@PathVariable Long studentId, @Valid @RequestBody AssignSubjectRequestDto request) {
        AssignSubjectResponseDto response = studentSubjectService.assignSubject(studentId,request.getSubjectId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



}
