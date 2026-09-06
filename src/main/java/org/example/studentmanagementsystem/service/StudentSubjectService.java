package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.domain.AssignSubjectResponseDto;
import org.example.studentmanagementsystem.entity.Student;
import org.example.studentmanagementsystem.entity.StudentSubject;
import org.example.studentmanagementsystem.entity.Subject;
import org.example.studentmanagementsystem.exception.DuplicateResourceException;
import org.example.studentmanagementsystem.exception.ResourceNotFoundException;
import org.example.studentmanagementsystem.repository.StudentRepository;
import org.example.studentmanagementsystem.repository.StudentSubjectRepository;
import org.example.studentmanagementsystem.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StudentSubjectService {
    private final StudentSubjectRepository studentSubjectRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;

    public StudentSubjectService(SubjectRepository subjectRepository, StudentRepository studentRepository, StudentSubjectRepository studentSubjectRepository){
        this.studentSubjectRepository = studentSubjectRepository;
        this.subjectRepository = subjectRepository;
        this.studentRepository = studentRepository;
    }

    public AssignSubjectResponseDto assignSubject(Long studentId, Long subjectId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Given Student not found: " + studentId+". Please check!!!"));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Given Subject not found: " + subjectId+". Please check!!!"));

        if (studentSubjectRepository.existsByStudentIdAndSubjectId(studentId, subjectId)) {
            throw new DuplicateResourceException("Subject already assigned to this student. Please check and choose another subject.");
        }

        StudentSubject assignment = new StudentSubject();
        assignment.setStudent(student);
        assignment.setSubject(subject);
        assignment.setAssignedAt(LocalDateTime.now());

        StudentSubject saved = studentSubjectRepository.save(assignment);

        AssignSubjectResponseDto response = new AssignSubjectResponseDto();
        response.setStudentId(saved.getStudent().getId());
        response.setSubjectId(saved.getSubject().getId());
        response.setAssignedAt(saved.getAssignedAt());

        return response;
    }
}
