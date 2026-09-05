package org.example.studentmanagementsystem.repository;

import org.example.studentmanagementsystem.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    boolean existsByName(String name);
    boolean existsByCode(String code);
}
