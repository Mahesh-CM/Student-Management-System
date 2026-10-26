package com.sms.model;

public class Faculty extends Person {

    public Faculty(int id, String name, String email, String phone, String department) {
        super(id, name, email, phone, department);
    }

    @Override
    public String getRole() { return "Faculty"; }
}
