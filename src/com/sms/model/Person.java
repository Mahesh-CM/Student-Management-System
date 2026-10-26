package com.sms.model;

/** Abstract base for people in the system (inheritance + encapsulation). */
public abstract class Person {

    private int id;
    private String name;
    private String email;
    private String phone;
    private String department;

    protected Person(int id, String name, String email, String phone, String department) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.department = department;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    /** Each subclass describes its own role (polymorphism). */
    public abstract String getRole();

    @Override
    public String toString() {
        return String.format("[%s #%d] %s | %s | %s | %s",
            getRole(), id, name, email, phone == null ? "-" : phone, department);
    }
}
