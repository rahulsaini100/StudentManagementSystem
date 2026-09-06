package org.example.studentmanagementsystem.domain;

import org.example.studentmanagementsystem.entity.Subject;

public class SubjectResponseDto {
    private Long id;
    private String name;
    private String code;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static SubjectResponseDto getSubjectResponseDto(Subject subject) {
        SubjectResponseDto responseDto = new SubjectResponseDto();
        responseDto.setId(subject.getId());
        responseDto.setName(subject.getName());
        responseDto.setCode(subject.getCode());
        return responseDto;
    }
}
