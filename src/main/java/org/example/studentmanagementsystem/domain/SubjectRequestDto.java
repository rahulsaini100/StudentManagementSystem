package org.example.studentmanagementsystem.domain;

import jakarta.validation.constraints.NotBlank;

public class SubjectRequestDto {
    @NotBlank(message = "Subject name is required")
    private String name;
    @NotBlank(message = "Subject code is required")
    private String code;

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
}
