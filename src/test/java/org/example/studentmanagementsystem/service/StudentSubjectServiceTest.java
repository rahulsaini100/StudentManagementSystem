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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentSubjectServiceTest {
    @Mock
    private StudentRepository studentRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private StudentSubjectRepository studentSubjectRepository;

    @InjectMocks
    private StudentSubjectService studentSubjectService;

    @Test
    void assignSubject_ThrowResourceNotFoundException_whenStudentDoesNotExist() {

        when(studentRepository.findById(25L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> studentSubjectService.assignSubject(25L, 10L));

        verify(studentSubjectRepository, never()).save(any());
    }

    @Test
    void assignSubject_ThrowResourceNotFoundException_whenSubjectDoesNotExist() {

        Student student = new Student();
        student.setId(18L);

        when(studentRepository.findById(18L)).thenReturn(Optional.of(student));
        when(subjectRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> studentSubjectService.assignSubject(18L, 10L));

        verify(studentSubjectRepository, never()).save(any());
    }

    @Test
    void assignSubject_ThrowDuplicateResourceException_whenAlreadyAssigned() {

        Student student = new Student();
        student.setId(15L);

        Subject subject = new Subject();
        subject.setId(12L);

        when(studentRepository.findById(15L)).thenReturn(Optional.of(student));
        when(subjectRepository.findById(12L)).thenReturn(Optional.of(subject));
        when(studentSubjectRepository.existsByStudentIdAndSubjectId(15L, 12L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> studentSubjectService.assignSubject(15L, 12L));

        verify(studentSubjectRepository, never()).save(any());
    }

    @Test
    void assignSubject_SaveAndReturnAssignment_whenValid() {

        Student student = new Student();
        student.setId(10L);

        Subject subject = new Subject();
        subject.setId(11L);

        when(studentRepository.findById(10L)).thenReturn(Optional.of(student));
        when(subjectRepository.findById(11L)).thenReturn(Optional.of(subject));
        when(studentSubjectRepository.existsByStudentIdAndSubjectId(10L, 11L)).thenReturn(false);

        StudentSubject saved = new StudentSubject();
        saved.setStudent(student);
        saved.setSubject(subject);
        saved.setAssignedAt(java.time.LocalDateTime.now());

        when(studentSubjectRepository.save(any(StudentSubject.class))).thenReturn(saved);

        AssignSubjectResponseDto response = studentSubjectService.assignSubject(10L, 11L);

        assertEquals(10L, response.getStudentId());
        assertEquals(11L, response.getSubjectId());
    }
}
