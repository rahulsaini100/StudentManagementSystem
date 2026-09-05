package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.domain.SubjectRequestDto;
import org.example.studentmanagementsystem.domain.SubjectResponseDto;
import org.example.studentmanagementsystem.entity.Subject;
import org.example.studentmanagementsystem.exception.DuplicateResourceException;
import org.example.studentmanagementsystem.exception.ResourceNotFoundException;
import org.example.studentmanagementsystem.exception.SubjectAssignedException;
import org.example.studentmanagementsystem.repository.StudentRepository;
import org.example.studentmanagementsystem.repository.StudentSubjectRepository;
import org.example.studentmanagementsystem.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final StudentSubjectRepository studentSubjectRepository;

    public  SubjectService(SubjectRepository subjectRepository, StudentSubjectRepository studentSubjectRepository){
        this.subjectRepository = subjectRepository;
        this.studentSubjectRepository = studentSubjectRepository;
    }

    public SubjectResponseDto addSubject(SubjectRequestDto request) {

        if (subjectRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Subject Already Exists with the given name");
        }
        if (subjectRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Subject Already Exists with the given code");
        }

        Subject subject = new Subject();
        subject.setName(request.getName());
        subject.setCode(request.getCode());
        subject.setCreatedAt(LocalDateTime.now());

        Subject saved = subjectRepository.save(subject);
        SubjectResponseDto responseDto = new SubjectResponseDto();
        responseDto.setId(saved.getId());
        responseDto.setName(saved.getName());
        responseDto.setCode(saved.getCode());
        return responseDto;
    }

    public void deleteSubject(Long id) {

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not Exits with the given Id"));

        if (studentSubjectRepository.existsBySubjectId(id)) {
            throw new SubjectAssignedException("Subject assigned with student can not be deleted");
        }

        subjectRepository.delete(subject);
    }

    public List<SubjectResponseDto> getAllSubjects(){
        List<SubjectResponseDto> allSubjects = new ArrayList<>();
        for(Subject subject : subjectRepository.findAll()) {
            SubjectResponseDto responseDto = new SubjectResponseDto();
            responseDto.setId(subject.getId());
            responseDto.setName(subject.getName());
            responseDto.setCode(subject.getCode());
            allSubjects.add(responseDto);
        }
        return allSubjects;
    }

}
