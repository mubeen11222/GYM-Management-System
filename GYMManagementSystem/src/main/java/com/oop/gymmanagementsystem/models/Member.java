package com.oop.gymmanagementsystem.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Member extends Person implements Serializable {
    private static final long serialVersionUID = 1L;

    private String memberId;
    private MembershipPlan plan;
    private String trainerId;
    private WorkoutPlan workoutPlan;
    private NutritionPlan nutritionPlan;
    private List<ProgressRecord> progressHistory;
    private boolean active;
    private String joinDate;
    private double currentWeight;
    private double height;
    private String fitnessGoal;

    public Member(String memberId, String name, int age, String gender,
                  String phone, String email, String address,
                  MembershipPlan plan, String joinDate, double currentWeight,
                  double height, String fitnessGoal) {
        super(name, age, gender, phone, email, address);
        this.memberId = memberId;
        this.plan = plan;
        this.trainerId = null;
        this.workoutPlan = null;
        this.nutritionPlan = null;
        this.progressHistory = new ArrayList<>();
        this.active = true;
        this.joinDate = joinDate;
        this.currentWeight = currentWeight;
        this.height = height;
        this.fitnessGoal = fitnessGoal;
    }

    @Override
    public String getPersonRole() { return "Member"; }

    @Override
    public String getId() { return memberId; }

    public boolean hasTrainer() { return trainerId != null; }

    // Getters
    public String getMemberId() { return memberId; }
    public MembershipPlan getPlan() { return plan; }
    public String getTrainerId() { return trainerId; }
    public WorkoutPlan getWorkoutPlan() { return workoutPlan; }
    public NutritionPlan getNutritionPlan() { return nutritionPlan; }
    public List<ProgressRecord> getProgressHistory() { return progressHistory; }
    public boolean isActive() { return active; }
    public String getJoinDate() { return joinDate; }
    public double getCurrentWeight() { return currentWeight; }
    public double getHeight() { return height; }
    public String getFitnessGoal() { return fitnessGoal; }

    // Setters
    public void setPlan(MembershipPlan plan) { this.plan = plan; }
    public void setTrainerId(String trainerId) { this.trainerId = trainerId; }
    public void setWorkoutPlan(WorkoutPlan workoutPlan) { this.workoutPlan = workoutPlan; }
    public void setNutritionPlan(NutritionPlan nutritionPlan) { this.nutritionPlan = nutritionPlan; }
    public void setActive(boolean active) { this.active = active; }
    public void setCurrentWeight(double currentWeight) { this.currentWeight = currentWeight; }
    public void setHeight(double height) { this.height = height; }
    public void setFitnessGoal(String fitnessGoal) { this.fitnessGoal = fitnessGoal; }

    public void addProgressRecord(ProgressRecord record) {
        progressHistory.add(record);
    }
}
