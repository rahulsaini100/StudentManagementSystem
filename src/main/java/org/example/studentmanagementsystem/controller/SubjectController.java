package org.example.studentmanagementsystem.controller;

import jakarta.validation.Valid;
import org.example.studentmanagementsystem.domain.StudentResponseDto;
import org.example.studentmanagementsystem.domain.SubjectRequestDto;
import org.example.studentmanagementsystem.domain.SubjectResponseDto;
import org.example.studentmanagementsystem.service.SubjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subjects")
public class SubjectController {
    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @PostMapping
    public ResponseEntity<SubjectResponseDto> addSubject(@Valid @RequestBody SubjectRequestDto request) {
        SubjectResponseDto response = subjectService.addSubject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SubjectResponseDto>> getAllSubjects() {
        List<SubjectResponseDto> subjects = subjectService.getAllSubjects();
        return ResponseEntity.status(HttpStatus.OK).body(subjects);
    }
}
