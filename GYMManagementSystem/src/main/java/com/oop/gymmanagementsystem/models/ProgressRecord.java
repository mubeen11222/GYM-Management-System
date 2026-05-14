package com.oop.gymmanagementsystem.models;

import java.io.Serializable;

public class ProgressRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private String date;
    private double weight;
    private double bodyFatPercentage;
    private int workoutsCompleted;
    private String notes;

    public ProgressRecord(String date, double weight, double bodyFatPercentage,
                          int workoutsCompleted, String notes) {
        this.date = date;
        this.weight = weight;
        this.bodyFatPercentage = bodyFatPercentage;
        this.workoutsCompleted = workoutsCompleted;
        this.notes = notes;
    }

    // Getters
    public String getDate() { return date; }
    public double getWeight() { return weight; }
    public double getBodyFatPercentage() { return bodyFatPercentage; }
    public int getWorkoutsCompleted() { return workoutsCompleted; }
    public String getNotes() { return notes; }

    // Setters
    public void setWeight(double weight) { this.weight = weight; }
    public void setBodyFatPercentage(double bodyFatPercentage) { this.bodyFatPercentage = bodyFatPercentage; }
    public void setWorkoutsCompleted(int workoutsCompleted) { this.workoutsCompleted = workoutsCompleted; }
    public void setNotes(String notes) { this.notes = notes; }
}
