package com.oop.gymmanagementsystem.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Trainer extends Person implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int MAX_MEMBERS = 5;

    private String trainerId;
    private String specialization;
    private double baseSalary;
    private List<String> assignedMemberIds;
    private String hireDate;
    private int experienceYears;

    public Trainer(String trainerId, String name, int age, String gender,
                   String phone, String email, String address,
                   String specialization, double baseSalary,
                   String hireDate, int experienceYears) {
        super(name, age, gender, phone, email, address);
        this.trainerId = trainerId;
        this.specialization = specialization;
        this.baseSalary = baseSalary;
        this.assignedMemberIds = new ArrayList<>();
        this.hireDate = hireDate;
        this.experienceYears = experienceYears;
    }

    @Override
    public String getPersonRole() { return "Trainer"; }

    @Override
    public String getId() { return trainerId; }

    public boolean canAcceptMember() {
        return assignedMemberIds.size() < MAX_MEMBERS;
    }

    public int getRemainingSlots() {
        return MAX_MEMBERS - assignedMemberIds.size();
    }

    public void addMember(String memberId) {
        if (canAcceptMember()) {
            assignedMemberIds.add(memberId);
        }
    }

    public void removeMember(String memberId) {
        assignedMemberIds.remove(memberId);
    }

    public boolean hasMember(String memberId) {
        return assignedMemberIds.contains(memberId);
    }

    // Getters
    public String getTrainerId() { return trainerId; }
    public String getSpecialization() { return specialization; }
    public double getBaseSalary() { return baseSalary; }
    public List<String> getAssignedMemberIds() { return assignedMemberIds; }
    public String getHireDate() { return hireDate; }
    public int getExperienceYears() { return experienceYears; }

    // Setters
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setBaseSalary(double baseSalary) { this.baseSalary = baseSalary; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }
}
