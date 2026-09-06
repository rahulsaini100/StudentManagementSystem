package org.example.studentmanagementsystem.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.example.studentmanagementsystem.entity.Student;

import java.time.LocalDateTime;

public class StudentRequestDto {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    private Integer age;

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

    public static Student getStudent(StudentRequestDto request) {
        Student student = new Student();
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setAge(request.getAge());
        student.setCreatedAt(LocalDateTime.now());
        student.setUpdatedAt(LocalDateTime.now());
        return student;
    }
}
