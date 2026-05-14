package com.oop.gymmanagementsystem.models;

import java.io.Serializable;

public abstract class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private int age;
    private String gender;
    private String phone;
    private String email;
    private String address;

    public Person(String name, int age, String gender, String phone, String email, String address) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    // Abstract method - polymorphism
    public abstract String getPersonRole();
    public abstract String getId();

    // Getters
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setGender(String gender) { this.gender = gender; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return name + " (" + getPersonRole() + ")";
    }
}
