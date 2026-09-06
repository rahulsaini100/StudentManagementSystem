package org.example.studentmanagementsystem.domain;

import org.example.studentmanagementsystem.entity.StudentSubject;

import java.time.LocalDateTime;

public class AssignSubjectResponseDto {
    private Long studentId;
    private Long subjectId;
    private LocalDateTime assignedAt;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public static AssignSubjectResponseDto getAssignSubjectResponseDto(StudentSubject saved) {
        AssignSubjectResponseDto response = new AssignSubjectResponseDto();
        response.setStudentId(saved.getStudent().getId());
        response.setSubjectId(saved.getSubject().getId());
        response.setAssignedAt(saved.getAssignedAt());
        return response;
    }
}
