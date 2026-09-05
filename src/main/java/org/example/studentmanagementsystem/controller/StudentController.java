package org.example.studentmanagementsystem.controller;

import jakarta.validation.Valid;
import org.example.studentmanagementsystem.domain.StudentRequestDto;
import org.example.studentmanagementsystem.domain.StudentResponseDto;
import org.example.studentmanagementsystem.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {
   private final StudentService studentService;

   public StudentController(StudentService studentService){
       this.studentService = studentService;
   }

   @PostMapping
    public ResponseEntity<StudentResponseDto> addStudent(@Valid @RequestBody StudentRequestDto request) {
       StudentResponseDto response = studentService.addStudent(request);
       return ResponseEntity.status(HttpStatus.CREATED).body(response);
   }

   @PutMapping("/{id}")
   public ResponseEntity<StudentResponseDto> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentRequestDto request) {
       StudentResponseDto response = studentService.updateStudent(id, request);
       return ResponseEntity.status(HttpStatus.OK).body(response);
   }

    @GetMapping
    public ResponseEntity<List<StudentResponseDto>> getAllStudents() {
        List<StudentResponseDto> students = studentService.getAllStudentsWithSubjects();
        return ResponseEntity.status(HttpStatus.OK).body(students);
    }

}
