package com.sms.model;

import java.time.LocalDate;

public class Student extends Person {

    private LocalDate dob;

    public Student(int id, String name, String email, String phone, LocalDate dob, String department) {
        super(id, name, email, phone, department);
        this.dob = dob;
    }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    @Override
    public String getRole() { return "Student"; }

    @Override
    public String toString() {
        return super.toString() + " | DOB: " + (dob == null ? "-" : dob);
    }
}
