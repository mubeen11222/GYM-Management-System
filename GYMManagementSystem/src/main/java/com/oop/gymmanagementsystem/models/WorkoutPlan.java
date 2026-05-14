package com.oop.gymmanagementsystem.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WorkoutPlan implements Serializable {
    private static final long serialVersionUID = 1L;

    private String planName;
    private String difficulty;
    private List<Exercise> exercises;
    private String assignedDate;

    public WorkoutPlan(String planName, String difficulty, String assignedDate) {
        this.planName = planName;
        this.difficulty = difficulty;
        this.exercises = new ArrayList<>();
        this.assignedDate = assignedDate;
    }

    public void addExercise(Exercise exercise) {
        exercises.add(exercise);
    }

    public void removeExercise(int index) {
        if (index >= 0 && index < exercises.size()) {
            exercises.remove(index);
        }
    }

    public int getCompletedCount() {
        return (int) exercises.stream().filter(Exercise::isCompleted).count();
    }

    public double getCompletionPercentage() {
        if (exercises.isEmpty()) return 0;
        return (getCompletedCount() * 100.0) / exercises.size();
    }

    // Getters
    public String getPlanName() { return planName; }
    public String getDifficulty() { return difficulty; }
    public List<Exercise> getExercises() { return exercises; }
    public String getAssignedDate() { return assignedDate; }

    // Setters
    public void setPlanName(String planName) { this.planName = planName; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
}
