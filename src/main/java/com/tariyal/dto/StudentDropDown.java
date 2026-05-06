package com.tariyal.dto;

public class StudentDropDown {

    private Long id;
    private String fullName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public StudentDropDown(Long id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }
    public StudentDropDown() {}
}
