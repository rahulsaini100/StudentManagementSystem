package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.domain.StudentRequestDto;
import org.example.studentmanagementsystem.domain.StudentResponseDto;
import org.example.studentmanagementsystem.domain.SubjectResponseDto;
import org.example.studentmanagementsystem.entity.Student;
import org.example.studentmanagementsystem.entity.StudentSubject;
import org.example.studentmanagementsystem.entity.Subject;
import org.example.studentmanagementsystem.exception.DuplicateResourceException;
import org.example.studentmanagementsystem.exception.ResourceNotFoundException;
import org.example.studentmanagementsystem.repository.StudentRepository;
import org.example.studentmanagementsystem.repository.StudentSubjectRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentSubjectRepository studentSubjectRepository;

    public StudentService(StudentRepository studentRepository, StudentSubjectRepository studentSubjectRepository) {
        this.studentRepository = studentRepository;
        this.studentSubjectRepository = studentSubjectRepository;
    }

    public StudentResponseDto addStudent(StudentRequestDto request) {

        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Student already present with given email: "+request.getEmail()+". Please check!!!");
        }
        Student student = StudentRequestDto.getStudent(request);
        Student saved = studentRepository.save(student);
        return StudentResponseDto.getStudentResponseDto(saved);
    }

    public StudentResponseDto updateStudent(Long id, StudentRequestDto request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with the given Id. Please check!!!"));

        if (studentRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new DuplicateResourceException("Given Email "+request.getEmail()+" already used by another student. Please check!!!");
        }

        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setAge(request.getAge());
        student.setUpdatedAt(LocalDateTime.now());
        Student saved = studentRepository.save(student);
        return StudentResponseDto.getStudentResponseDto(saved);
    }

    public List<StudentResponseDto> getAllStudentsWithSubjects() {

        List<Student> students = studentRepository.findAll();
        List<StudentResponseDto> result = new ArrayList<>();

        for (Student student : students) {
            List<StudentSubject> assignedStudentSubject = studentSubjectRepository.findByStudentId(student.getId());
            List<SubjectResponseDto> subjectDtos = new ArrayList<>();
            for (StudentSubject studentSubject : assignedStudentSubject) {
                Subject subject = studentSubject.getSubject();
                SubjectResponseDto subjectDto = new SubjectResponseDto();
                subjectDto.setId(subject.getId());
                subjectDto.setName(subject.getName());
                subjectDto.setCode(subject.getCode());
                subjectDtos.add(subjectDto);
            }
            StudentResponseDto studentDto = StudentResponseDto.getStudentResponseDto(student);
            studentDto.setSubjects(subjectDtos);

            result.add(studentDto);
        }
        return result;
    }
}
