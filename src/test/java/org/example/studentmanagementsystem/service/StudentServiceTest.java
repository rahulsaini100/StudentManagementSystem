package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.domain.StudentRequestDto;
import org.example.studentmanagementsystem.domain.StudentResponseDto;
import org.example.studentmanagementsystem.entity.Student;
import org.example.studentmanagementsystem.entity.StudentSubject;
import org.example.studentmanagementsystem.entity.Subject;
import org.example.studentmanagementsystem.exception.DuplicateResourceException;
import org.example.studentmanagementsystem.exception.ResourceNotFoundException;
import org.example.studentmanagementsystem.repository.StudentRepository;
import org.example.studentmanagementsystem.repository.StudentSubjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {
    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentSubjectRepository studentSubjectRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    void addStudent_SaveAndReturnStudent_whenEmailIsNotDuplicate() {

        StudentRequestDto request = new StudentRequestDto();
        request.setName("Sahil Sharma");
        request.setEmail("sahil123@gmail.com");
        request.setAge(20);

        when(studentRepository.existsByEmail("sahil123@gmail.com")).thenReturn(false);

        Student savedStudent = new Student();
        savedStudent.setId(1L);
        savedStudent.setName("Sahil Sharma");
        savedStudent.setEmail("sahil123@gmail.com");
        savedStudent.setAge(20);

        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);

        StudentResponseDto response = studentService.addStudent(request);
        assertEquals(1L, response.getId());
        assertEquals("Sahil Sharma", response.getName());
        assertEquals("sahil123@gmail.com", response.getEmail());
    }

    @Test
    void addStudent_ThrowDuplicateResourceException_whenEmailAlreadyExists() {

        StudentRequestDto request = new StudentRequestDto();
        request.setEmail("sahil123@gmail.com");

        when(studentRepository.existsByEmail("sahil123@gmail.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> studentService.addStudent(request));
        verify(studentRepository, never()).save(any());
    }

    @Test
    void updateStudent_ThrowResourceNotFoundException_whenStudentDoesNotExist() {

        when(studentRepository.findById(10L)).thenReturn(Optional.empty());

        StudentRequestDto request = new StudentRequestDto();
        request.setName("test");

        assertThrows(ResourceNotFoundException.class,
                () -> studentService.updateStudent(10L, request));
        verify(studentRepository, never()).save(any());
    }

    @Test
    void updateStudent_UpdateAndReturnStudent_whenStudentExists() {

        Student existingStudent = new Student();
        existingStudent.setId(1L);
        existingStudent.setName("Rahul");
        existingStudent.setEmail("Rahul@gmail.com");
        existingStudent.setAge(20);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existingStudent));

        StudentRequestDto request = new StudentRequestDto();
        request.setName("Rahul Saini");
        request.setEmail("Rahul@gmail.com");
        request.setAge(21);

        when(studentRepository.save(any(Student.class))).thenReturn(existingStudent);

        StudentResponseDto response = studentService.updateStudent(1L, request);

        assertEquals("Rahul Saini", response.getName());
        assertEquals("Rahul@gmail.com", response.getEmail());
        assertEquals(21, response.getAge());
    }

    @Test
    void getAllStudentsWithSubjects_ReturnStudentsWithNestedSubjects() {

        Student student = new Student();
        student.setId(1L);
        student.setName("Ravi");
        student.setEmail("Ravi12345@gmail.com");
        student.setAge(20);

        when(studentRepository.findAll()).thenReturn(List.of(student));

        Subject subject = new Subject();
        subject.setId(10L);
        subject.setName("Science");
        subject.setCode("SCI1");

        StudentSubject assign = new StudentSubject();
        assign.setStudent(student);
        assign.setSubject(subject);

        when(studentSubjectRepository.findByStudentId(1L)).thenReturn(List.of(assign));

        List<StudentResponseDto> result = studentService.getAllStudentsWithSubjects();

        assertEquals(1, result.size());
        assertEquals("Ravi", result.getFirst().getName());
        assertEquals(1, result.getFirst().getSubjects().size());
        assertEquals("Science", result.getFirst().getSubjects().getFirst().getName());
    }
}
