package com.oop.gymmanagementsystem.models;

import java.io.Serializable;

public class Exercise implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private int sets;
    private int reps;
    private int restSeconds;
    private String muscleGroup;
    private boolean completed;

    public Exercise(String name, int sets, int reps, int restSeconds, String muscleGroup) {
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.restSeconds = restSeconds;
        this.muscleGroup = muscleGroup;
        this.completed = false;
    }

    // Getters
    public String getName() { return name; }
    public int getSets() { return sets; }
    public int getReps() { return reps; }
    public int getRestSeconds() { return restSeconds; }
    public String getMuscleGroup() { return muscleGroup; }
    public boolean isCompleted() { return completed; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setSets(int sets) { this.sets = sets; }
    public void setReps(int reps) { this.reps = reps; }
    public void setRestSeconds(int restSeconds) { this.restSeconds = restSeconds; }
    public void setMuscleGroup(String muscleGroup) { this.muscleGroup = muscleGroup; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    @Override
    public String toString() {
        return name + " - " + sets + "x" + reps + " (Rest: " + restSeconds + "s)";
    }
}
