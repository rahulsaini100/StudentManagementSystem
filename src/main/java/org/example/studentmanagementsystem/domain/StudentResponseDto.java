package org.example.studentmanagementsystem.domain;

import org.example.studentmanagementsystem.entity.Student;

import java.util.List;

public class StudentResponseDto {
    private Long id;
    private String name;
    private String email;
    private Integer age;
    private List<SubjectResponseDto> subjects;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public List<SubjectResponseDto> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<SubjectResponseDto> subjects) {
        this.subjects = subjects;
    }

    public static StudentResponseDto getStudentResponseDto(Student saved) {
        StudentResponseDto response = new StudentResponseDto();
        response.setId(saved.getId());
        response.setName(saved.getName());
        response.setEmail(saved.getEmail());
        response.setAge(saved.getAge());
        return response;
    }
}
