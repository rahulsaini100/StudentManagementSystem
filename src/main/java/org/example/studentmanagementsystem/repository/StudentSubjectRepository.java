package org.example.studentmanagementsystem.repository;

import org.example.studentmanagementsystem.entity.StudentSubject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentSubjectRepository extends JpaRepository<StudentSubject, Long> {
    boolean existsBySubjectId(Long subjectId);
    boolean existsByStudentIdAndSubjectId(Long studentId, Long subjectId);
    List<StudentSubject> findByStudentId(Long studentId);
}
