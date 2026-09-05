package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.domain.SubjectRequestDto;
import org.example.studentmanagementsystem.domain.SubjectResponseDto;
import org.example.studentmanagementsystem.entity.Subject;
import org.example.studentmanagementsystem.exception.DuplicateResourceException;
import org.example.studentmanagementsystem.exception.ResourceNotFoundException;
import org.example.studentmanagementsystem.exception.SubjectAssignedException;
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
public class SubjectServiceTest {
    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private StudentSubjectRepository studentSubjectRepository;

    @InjectMocks
    private SubjectService subjectService;

    @Test
    void addSubject_ThrowDuplicateResourceException_whenNameAlreadyExists() {

        SubjectRequestDto request = new SubjectRequestDto();
        request.setName("English");
        request.setCode("ENG1");

        when(subjectRepository.existsByName("English")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> subjectService.addSubject(request));

        verify(subjectRepository, never()).save(any());
    }

    @Test
    void addSubject_ThrowDuplicateResourceException_whenCodeAlreadyExists() {

        SubjectRequestDto request = new SubjectRequestDto();
        request.setName("Physics");
        request.setCode("PHY1");

        when(subjectRepository.existsByName("Physics")).thenReturn(false);
        when(subjectRepository.existsByCode("PHY1")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> subjectService.addSubject(request));

        verify(subjectRepository, never()).save(any());
    }

    @Test
    void addSubject_SaveAndReturnSubject_whenNoDuplicates() {

        SubjectRequestDto request = new SubjectRequestDto();
        request.setName("Chemistry");
        request.setCode("CHE1");

        when(subjectRepository.existsByName("Chemistry")).thenReturn(false);
        when(subjectRepository.existsByCode("CHE1")).thenReturn(false);

        Subject saved = new Subject();
        saved.setId(1L);
        saved.setName("Chemistry");
        saved.setCode("CHE1");

        when(subjectRepository.save(any(Subject.class))).thenReturn(saved);

        SubjectResponseDto response = subjectService.addSubject(request);

        assertEquals(1L, response.getId());
        assertEquals("Chemistry", response.getName());
        assertEquals("CHE1", response.getCode());
    }

    @Test
    void deleteSubject_ThrowResourceNotFoundException_whenSubjectDoesNotExist() {

        when(subjectRepository.findById(20L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> subjectService.deleteSubject(20L));

        verify(subjectRepository, never()).delete(any());
    }

    @Test
    void deleteSubject_ThrowSubjectAssignedException_whenSubjectIsAssigned() {

        Subject subject = new Subject();
        subject.setId(15L);

        when(subjectRepository.findById(15L)).thenReturn(Optional.of(subject));
        when(studentSubjectRepository.existsBySubjectId(15L)).thenReturn(true);

        assertThrows(SubjectAssignedException.class,
                () -> subjectService.deleteSubject(15L));

        verify(subjectRepository, never()).delete(any());
    }

    @Test
    void deleteSubject_DeleteSuccessfully_whenSubjectExistsAndIsNotAssigned() {

        Subject subject = new Subject();
        subject.setId(20L);

        when(subjectRepository.findById(20L)).thenReturn(Optional.of(subject));
        when(studentSubjectRepository.existsBySubjectId(20L)).thenReturn(false);

        subjectService.deleteSubject(20L);

        verify(subjectRepository).delete(subject);
    }

    @Test
    void getAllSubjects_ReturnMappedList() {

        Subject subject = new Subject();
        subject.setId(1L);
        subject.setName("Chemistry");
        subject.setCode("CHE2");

        when(subjectRepository.findAll()).thenReturn(java.util.List.of(subject));

        var result = subjectService.getAllSubjects();

        assertEquals(1, result.size());
        assertEquals("Chemistry", result.getFirst().getName());
        assertEquals("CHE2", result.getFirst().getCode());
    }
}
