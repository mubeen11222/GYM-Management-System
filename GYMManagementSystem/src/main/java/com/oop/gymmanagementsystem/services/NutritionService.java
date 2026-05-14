package com.oop.gymmanagementsystem.services;

import com.oop.gymmanagementsystem.models.Member;
import com.oop.gymmanagementsystem.models.NutritionPlan;

public class NutritionService {

    public void generatePlan(Member member) {
        double weight = member.getCurrentWeight();
        double height = member.getHeight();
        int age = member.getAge();
        String goal = member.getFitnessGoal() != null ? member.getFitnessGoal() : "General Fitness";

        // AI-simulated BMR calculation (Mifflin-St Jeor)
        double bmr;
        if ("Male".equalsIgnoreCase(member.getGender())) {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5;
        } else {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) - 161;
        }

        double tdee = bmr * 1.55; // Moderate activity multiplier
        int calories;
        double protein, carbs, fat;

        switch (goal.toLowerCase()) {
            case "weight loss":
                calories = (int) (tdee - 500);
                protein = weight * 2.0;
                fat = weight * 0.8;
                carbs = (calories - (protein * 4) - (fat * 9)) / 4;
                break;
            case "muscle gain":
                calories = (int) (tdee + 400);
                protein = weight * 2.2;
                fat = weight * 1.0;
                carbs = (calories - (protein * 4) - (fat * 9)) / 4;
                break;
            case "strength":
                calories = (int) (tdee + 300);
                protein = weight * 2.0;
                fat = weight * 1.0;
                carbs = (calories - (protein * 4) - (fat * 9)) / 4;
                break;
            default:
                calories = (int) tdee;
                protein = weight * 1.6;
                fat = weight * 0.9;
                carbs = (calories - (protein * 4) - (fat * 9)) / 4;
                break;
        }

        NutritionPlan plan = new NutritionPlan(calories, protein, Math.max(carbs, 50), fat, goal);

        // Generate AI meal suggestions
        plan.addMeal("Breakfast: Oatmeal with banana, 3 eggs, black coffee (" + (int)(calories * 0.25) + " cal)");
        plan.addMeal("Mid-Morning: Protein shake with almonds (" + (int)(calories * 0.10) + " cal)");
        plan.addMeal("Lunch: Grilled chicken breast, brown rice, vegetables (" + (int)(calories * 0.30) + " cal)");
        plan.addMeal("Pre-Workout: Apple with peanut butter (" + (int)(calories * 0.10) + " cal)");
        plan.addMeal("Dinner: Salmon, sweet potato, mixed salad (" + (int)(calories * 0.25) + " cal)");

        member.setNutritionPlan(plan);
    }
}
