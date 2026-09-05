package org.example.studentmanagementsystem.domain;

import jakarta.validation.constraints.NotNull;

public class AssignSubjectRequestDto {
    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }
}
