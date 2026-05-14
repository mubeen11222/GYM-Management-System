package com.oop.gymmanagementsystem.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NutritionPlan implements Serializable {
    private static final long serialVersionUID = 1L;

    private int targetCalories;
    private double proteinGrams;
    private double carbsGrams;
    private double fatGrams;
    private List<String> meals;
    private String goal;

    public NutritionPlan(int targetCalories, double proteinGrams, double carbsGrams,
                         double fatGrams, String goal) {
        this.targetCalories = targetCalories;
        this.proteinGrams = proteinGrams;
        this.carbsGrams = carbsGrams;
        this.fatGrams = fatGrams;
        this.goal = goal;
        this.meals = new ArrayList<>();
    }

    public void addMeal(String meal) {
        meals.add(meal);
    }

    // Getters
    public int getTargetCalories() { return targetCalories; }
    public double getProteinGrams() { return proteinGrams; }
    public double getCarbsGrams() { return carbsGrams; }
    public double getFatGrams() { return fatGrams; }
    public List<String> getMeals() { return meals; }
    public String getGoal() { return goal; }

    // Setters
    public void setTargetCalories(int targetCalories) { this.targetCalories = targetCalories; }
    public void setProteinGrams(double proteinGrams) { this.proteinGrams = proteinGrams; }
    public void setCarbsGrams(double carbsGrams) { this.carbsGrams = carbsGrams; }
    public void setFatGrams(double fatGrams) { this.fatGrams = fatGrams; }
    public void setGoal(String goal) { this.goal = goal; }
}
